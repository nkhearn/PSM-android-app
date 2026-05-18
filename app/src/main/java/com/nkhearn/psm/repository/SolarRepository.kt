package com.nkhearn.psm.repository

import com.nkhearn.psm.api.SolarApiService
import com.nkhearn.psm.models.LastDataResponse
import com.nkhearn.psm.websocket.SolarWebSocketListener
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.WebSocket
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class SolarRepository(private val host: String, private val port: Int) {

    private val _currentData = MutableStateFlow<LastDataResponse?>(null)
    val currentData: StateFlow<LastDataResponse?> = _currentData

    private val _isConnected = MutableStateFlow(false)
    val isConnected: StateFlow<Boolean> = _isConnected

    private val okHttpClient = OkHttpClient()
    private var webSocket: WebSocket? = null

    private val apiService: SolarApiService by lazy {
        Retrofit.Builder()
            .baseUrl("http://$host:$port")
            .addConverterFactory(GsonConverterFactory.create())
            .client(okHttpClient)
            .build()
            .create(SolarApiService::class.java)
    }

    fun connectWebSocket() {
        val request = Request.Builder()
            .url("ws://$host:$port/ws")
            .build()

        webSocket?.close(1000, "Reconnecting")
        webSocket = okHttpClient.newWebSocket(request, SolarWebSocketListener(
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
            android.util.Log.e("SolarRepository", "Error fetching last data", e)
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
