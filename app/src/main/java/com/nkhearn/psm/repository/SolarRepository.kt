package com.nkhearn.psm.repository

import com.google.gson.Gson
import com.nkhearn.psm.api.SolarApiService
import com.nkhearn.psm.models.LastDataResponse
import com.nkhearn.psm.websocket.SolarWebSocketListener
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.WebSocket
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class SolarRepository(private val host: String, private val port: Int) {

    private val _currentData = MutableStateFlow<LastDataResponse?>(null)
    val currentData: StateFlow<LastDataResponse?> = _currentData.asStateFlow()

    private val _isConnected = MutableStateFlow(false)
    val isConnected: StateFlow<Boolean> = _isConnected.asStateFlow()

    private var webSocket: WebSocket? = null

    companion object {
        private val loggingInterceptor = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }

        private val sharedOkHttpClient = OkHttpClient.Builder()
            .addInterceptor(loggingInterceptor)
            .build()

        private val sharedGson = Gson()
    }

    private val apiService: SolarApiService by lazy {
        Retrofit.Builder()
            .baseUrl("http://$host:$port")
            .addConverterFactory(GsonConverterFactory.create(sharedGson))
            .client(sharedOkHttpClient)
            .build()
            .create(SolarApiService::class.java)
    }

    fun connectWebSocket() {
        val request = Request.Builder()
            .url("ws://$host:$port/ws")
            .build()

        webSocket?.close(1000, "Reconnecting")
        webSocket = sharedOkHttpClient.newWebSocket(request, SolarWebSocketListener(
            gson = sharedGson,
            onMessageReceived = { message ->
                if (message.type == "new_data") {
                    _currentData.value = message.payload
                }
            },
            onStatusChanged = { status ->
                _isConnected.value = status
            }
        ))
    }

    suspend fun fetchLastData() {
        try {
            val response = apiService.getLastData()
            _currentData.value = response
        } catch (e: Exception) {
            // Handle error
        }
    }

    suspend fun getMetricHistory(key: String): List<Double> {
        return try {
            val history = apiService.getMetricHistory(key, limit = 50)
            history.mapNotNull { it.getOrNull(1)?.toString()?.toDoubleOrNull() }
        } catch (e: Exception) {
            emptyList()
        }
    }

    fun disconnect() {
        webSocket?.close(1000, "Disconnecting")
    }
}
