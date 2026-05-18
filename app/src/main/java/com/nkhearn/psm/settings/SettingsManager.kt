package com.nkhearn.psm.settings

import android.content.Context
import android.content.SharedPreferences

class SettingsManager(context: Context) {
    private val prefs: SharedPreferences = context.getSharedPreferences("psm_settings", Context.MODE_PRIVATE)

    companion object {
        private const val KEY_HOST = "host"
        private const val KEY_PORT = "port"
        private const val DEFAULT_HOST = "192.168.1.100"
        private const val DEFAULT_PORT = 8000
    }

    var host: String
        get() = prefs.getString(KEY_HOST, DEFAULT_HOST) ?: DEFAULT_HOST
        set(value) = prefs.edit().putString(KEY_HOST, value).apply()

    var port: Int
        get() = prefs.getInt(KEY_PORT, DEFAULT_PORT)
        set(value) = prefs.edit().putInt(KEY_PORT, value).apply()
}
