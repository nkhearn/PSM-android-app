package com.nkhearn.psm.widgets

import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context
import android.widget.RemoteViews
import com.nkhearn.psm.R
import com.nkhearn.psm.repository.SolarRepository
import com.nkhearn.psm.settings.SettingsManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch

class SolarMetricWidget : AppWidgetProvider() {
    private val job = SupervisorJob()
    private val scope = CoroutineScope(Dispatchers.IO + job)

    override fun onUpdate(context: Context, appWidgetManager: AppWidgetManager, appWidgetIds: IntArray) {
        for (appWidgetId in appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId)
        }
    }

    private fun updateAppWidget(context: Context, appWidgetManager: AppWidgetManager, appWidgetId: Int) {
        val settingsManager = SettingsManager(context)
        val repository = SolarRepository(settingsManager.host, settingsManager.port)

        val views = RemoteViews(context.packageName, R.layout.widget_solar_metric)

        scope.launch {
            try {
                repository.fetchLastData()
                val data = repository.currentData.value
                if (data != null) {
                    val metricKey = "pv_power"
                    val value = data.data[metricKey] ?: "N/A"

                    views.setTextViewText(R.id.widget_title, metricKey.replace("_", " ").replaceFirstChar { it.uppercase() })
                    views.setTextViewText(R.id.widget_value, value.toString())
                    views.setTextViewText(R.id.widget_timestamp, data.timestamp)
                } else {
                    views.setTextViewText(R.id.widget_timestamp, "No data received")
                }
            } catch (e: Exception) {
                views.setTextViewText(R.id.widget_timestamp, "Error: ${e.message}")
            }
            appWidgetManager.updateAppWidget(appWidgetId, views)
        }
    }
}
