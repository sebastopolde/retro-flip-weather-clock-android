package fr.papycha.htcweatherclock.work

import android.appwidget.AppWidgetManager
import android.content.Context
import androidx.work.Worker
import androidx.work.WorkerParameters
import fr.papycha.htcweatherclock.storage.WidgetPreferences
import fr.papycha.htcweatherclock.weather.OpenMeteoClient
import fr.papycha.htcweatherclock.widget.WidgetRenderer

class WeatherRefreshWorker(context: Context, params: WorkerParameters) : Worker(context, params) {
    override fun doWork(): Result {
        val id = inputData.getInt(KEY_WIDGET_ID, AppWidgetManager.INVALID_APPWIDGET_ID)
        if (id == AppWidgetManager.INVALID_APPWIDGET_ID) return Result.failure()
        val previous = WidgetPreferences.weather(applicationContext, id)
        val result = OpenMeteoClient.fetch(WidgetPreferences.city(applicationContext, id), previous)
        result.snapshot?.let { WidgetPreferences.setWeather(applicationContext, it, id) }
        AppWidgetManager.getInstance(applicationContext).updateAppWidget(id, WidgetRenderer.render(applicationContext, id))
        return when (RefreshDecision.from(result)) {
            RefreshDecision.COMPLETE -> Result.success()
            RefreshDecision.RETRY -> Result.retry()
        }
    }

    companion object { const val KEY_WIDGET_ID = "widget_id" }
}
