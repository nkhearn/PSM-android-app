package com.nkhearn.psm.widgets

import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context
import androidx.work.Data
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import com.nkhearn.psm.R

class SolarMetricWidget : AppWidgetProvider() {

    override fun onUpdate(context: Context, appWidgetManager: AppWidgetManager, appWidgetIds: IntArray) {
        for (appWidgetId in appWidgetIds) {
            val data = Data.Builder()
                .putInt(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId)
                .build()

            val workRequest = OneTimeWorkRequestBuilder<SolarWidgetWorker>()
                .setInputData(data)
                .build()

            WorkManager.getInstance(context).enqueue(workRequest)
        }
    }
}
