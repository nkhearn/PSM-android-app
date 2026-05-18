package com.nkhearn.psm.widgets

import android.content.Context
import android.appwidget.AppWidgetManager
import android.widget.RemoteViews
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.nkhearn.psm.R
import com.nkhearn.psm.repository.SolarRepository
import com.nkhearn.psm.settings.SettingsManager

class SolarWidgetWorker(
    appContext: Context,
    workerParams: WorkerParameters
) : CoroutineWorker(appContext, workerParams) {

    override suspend fun doWork(): Result {
        val appWidgetId = inputData.getInt(AppWidgetManager.EXTRA_APPWIDGET_ID, AppWidgetManager.INVALID_APPWIDGET_ID)
        if (appWidgetId == AppWidgetManager.INVALID_APPWIDGET_ID) return Result.failure()

        val settingsManager = SettingsManager(applicationContext)
        val repository = SolarRepository(settingsManager.host, settingsManager.port)
        val appWidgetManager = AppWidgetManager.getInstance(applicationContext)
        val views = RemoteViews(applicationContext.packageName, R.layout.widget_solar_metric)

        return try {
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
            appWidgetManager.updateAppWidget(appWidgetId, views)
            Result.success()
        } catch (e: Exception) {
            views.setTextViewText(R.id.widget_timestamp, "Error: ${e.message}")
            appWidgetManager.updateAppWidget(appWidgetId, views)
            Result.retry()
        }
    }
}
