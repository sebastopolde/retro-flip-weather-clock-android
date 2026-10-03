package fr.papycha.htcweatherclock.widget

import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import fr.papycha.htcweatherclock.storage.WidgetPreferences
import fr.papycha.htcweatherclock.weather.OpenMeteoClient

class FlipClockWidgetProvider : AppWidgetProvider() {
    override fun onUpdate(context: Context, manager: AppWidgetManager, ids: IntArray) { ids.forEach { manager.updateAppWidget(it, WidgetRenderer.render(context, it)) }; refresh(context, ids) }
    override fun onAppWidgetOptionsChanged(context: Context, manager: AppWidgetManager, id: Int, options: android.os.Bundle) { manager.updateAppWidget(id, WidgetRenderer.render(context, id)) }
    override fun onDeleted(context: Context, ids: IntArray) = ids.forEach { WidgetPreferences.delete(context, it) }
    override fun onReceive(context: Context, intent: Intent) {
        super.onReceive(context, intent)
        if (intent.action == ACTION_REFRESH) { val id = intent.getIntExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, AppWidgetManager.INVALID_APPWIDGET_ID); refresh(context, if (id < 0) AppWidgetManager.getInstance(context).getAppWidgetIds(ComponentName(context, javaClass)) else intArrayOf(id)) }
    }
    private fun refresh(context: Context, ids: IntArray) {
        val pending = goAsync(); Thread { try { val manager = AppWidgetManager.getInstance(context); ids.forEach { id -> val result = OpenMeteoClient.fetch(WidgetPreferences.city(context, id), WidgetPreferences.weather(context, id)); result.snapshot?.let { WidgetPreferences.setWeather(context, it, id) }; manager.updateAppWidget(id, WidgetRenderer.render(context, id)) } } finally { pending.finish() } }.start()
    }
    companion object { const val ACTION_REFRESH = "fr.papycha.htcweatherclock.REFRESH" }
}
