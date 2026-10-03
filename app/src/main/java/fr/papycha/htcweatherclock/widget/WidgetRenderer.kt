package fr.papycha.htcweatherclock.widget

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.content.Context
import android.content.Intent
import android.os.Build
import android.widget.RemoteViews
import fr.papycha.htcweatherclock.MainActivity
import fr.papycha.htcweatherclock.R
import fr.papycha.htcweatherclock.storage.WidgetPreferences
import fr.papycha.htcweatherclock.weather.HtcWeatherPresentation
import fr.papycha.htcweatherclock.weather.HtcWeatherDrawable

object WidgetRenderer {
    fun render(context: Context, appWidgetId: Int): RemoteViews {
        val manager = AppWidgetManager.getInstance(context)
        val width = manager.getAppWidgetOptions(appWidgetId).getInt(AppWidgetManager.OPTION_APPWIDGET_MIN_WIDTH, 250)
        val layout = when (WidgetLayoutSelector.select(width, Build.VERSION.SDK_INT)) { WidgetLayout.COMPACT -> R.layout.widget_compact; WidgetLayout.CLASSIC -> R.layout.widget_classic; WidgetLayout.WIDE -> R.layout.widget_wide }
        val weather = WidgetPreferences.weather(context, appWidgetId)
        return RemoteViews(context.packageName, layout).apply {
            val visual = weather?.let { HtcWeatherPresentation.forWmo(it.weatherCode) }
            setImageViewResource(R.id.weather_icon, HtcWeatherDrawable.forCode(visual?.iconCode ?: 7))
            setTextViewText(R.id.temperature, weather?.let { "${it.temperature}°" } ?: "--°")
            setTextViewText(R.id.city, weather?.city ?: context.getString(R.string.tap_to_configure))
            setTextViewText(R.id.condition, visual?.label ?: context.getString(R.string.weather_offline))
            setTextViewText(R.id.range, weather?.let { "${it.maximum}° / ${it.minimum}°" } ?: "--° / --°")
            val openIntent = Intent(context, MainActivity::class.java).putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId)
            setOnClickPendingIntent(R.id.widget_root, PendingIntent.getActivity(context, appWidgetId, openIntent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE))
            val refreshIntent = Intent(context, WidgetRefreshReceiver::class.java).putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId)
            setOnClickPendingIntent(R.id.weather_panel, PendingIntent.getBroadcast(context, appWidgetId, refreshIntent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE))
        }
    }
}
