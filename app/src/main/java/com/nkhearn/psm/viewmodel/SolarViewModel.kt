package com.nkhearn.psm.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nkhearn.psm.models.LastDataResponse
import com.nkhearn.psm.repository.SolarRepository
import com.nkhearn.psm.settings.SettingsManager
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class SolarViewModel(private val settingsManager: SettingsManager) : ViewModel() {

    private var repository: SolarRepository? = null

    private val _currentData = MutableStateFlow<LastDataResponse?>(null)
    val currentData: StateFlow<LastDataResponse?> = _currentData.asStateFlow()

    private val _isConnected = MutableStateFlow(false)
    val isConnected: StateFlow<Boolean> = _isConnected.asStateFlow()

    private val _metricHistory = MutableStateFlow<Map<String, List<Double>>>(emptyMap())
    val metricHistory: StateFlow<Map<String, List<Double>>> = _metricHistory.asStateFlow()

    private var dataJob: Job? = null
    private var statusJob: Job? = null

    init {
        connect()
    }

    fun connect() {
        repository?.disconnect()
        dataJob?.cancel()
        statusJob?.cancel()
        _metricHistory.value = emptyMap() // Reset history on new connection

        val newRepo = SolarRepository(settingsManager.host, settingsManager.port)
        repository = newRepo

        dataJob = viewModelScope.launch {
            newRepo.currentData.collect { data ->
                _currentData.value = data
                data?.data?.keys?.forEach { key ->
                    // Only fetch history if we don't have it yet for this session/connection
                    if (!_metricHistory.value.containsKey(key) && key != "timestamp") {
                        fetchHistory(key)
                    }
                }
            }
        }

        statusJob = viewModelScope.launch {
            newRepo.isConnected.collect {
                _isConnected.value = it
            }
        }

        viewModelScope.launch {
            newRepo.fetchLastData()
            newRepo.connectWebSocket()
        }
    }

    private fun fetchHistory(key: String) {
        viewModelScope.launch {
            repository?.let { repo ->
                val history = repo.getMetricHistory(key)
                val currentMap = _metricHistory.value.toMutableMap()
                currentMap[key] = history
                _metricHistory.value = currentMap
            }
        }
    }

    fun updateSettings(host: String, port: Int) {
        settingsManager.host = host
        settingsManager.port = port
        connect()
    }

    override fun onCleared() {
        super.onCleared()
        repository?.disconnect()
    }

    fun getHost() = settingsManager.host
    fun getPort() = settingsManager.port
}
