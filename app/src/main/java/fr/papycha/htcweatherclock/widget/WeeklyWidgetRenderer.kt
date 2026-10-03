package fr.papycha.htcweatherclock.widget

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.content.Context
import android.content.Intent
import android.view.View
import android.widget.RemoteViews
import fr.papycha.htcweatherclock.MainActivity
import fr.papycha.htcweatherclock.R
import fr.papycha.htcweatherclock.storage.WidgetPreferences
import fr.papycha.htcweatherclock.weather.ForecastDayFormatter
import fr.papycha.htcweatherclock.weather.HtcWeatherDrawable
import fr.papycha.htcweatherclock.weather.HtcWeatherPresentation
import java.util.Locale

object WeeklyWidgetRenderer {
    private val dayLabels = intArrayOf(R.id.day_1_label, R.id.day_2_label, R.id.day_3_label, R.id.day_4_label, R.id.day_5_label, R.id.day_6_label, R.id.day_7_label)
    private val dayIcons = intArrayOf(R.id.day_1_icon, R.id.day_2_icon, R.id.day_3_icon, R.id.day_4_icon, R.id.day_5_icon, R.id.day_6_icon, R.id.day_7_icon)
    private val dayTemperatures = intArrayOf(R.id.day_1_temperature, R.id.day_2_temperature, R.id.day_3_temperature, R.id.day_4_temperature, R.id.day_5_temperature, R.id.day_6_temperature, R.id.day_7_temperature)
    private val dayColumns = intArrayOf(R.id.day_1, R.id.day_2, R.id.day_3, R.id.day_4, R.id.day_5, R.id.day_6, R.id.day_7)

    fun render(context: Context, appWidgetId: Int): RemoteViews {
        val weather = WidgetPreferences.weather(context, appWidgetId)
        val visual = weather?.let { HtcWeatherPresentation.forWmo(it.weatherCode) }
        return RemoteViews(context.packageName, R.layout.widget_weekly).apply {
            setImageViewResource(R.id.weather_icon, HtcWeatherDrawable.forCode(visual?.iconCode ?: 7))
            setTextViewText(R.id.temperature, weather?.let { "${it.temperature}°" } ?: "--°")
            setTextViewText(R.id.city, weather?.city ?: context.getString(R.string.tap_to_configure))
            setTextViewText(R.id.condition, visual?.label ?: context.getString(R.string.weather_offline))
            setTextViewText(R.id.range, weather?.let { "${it.maximum}° / ${it.minimum}°" } ?: "--° / --°")

            dayColumns.indices.forEach { index ->
                val day = weather?.forecast?.getOrNull(index)
                setViewVisibility(dayColumns[index], if (day == null) View.INVISIBLE else View.VISIBLE)
                if (day != null) {
                    val dayVisual = HtcWeatherPresentation.forWmo(day.weatherCode)
                    setTextViewText(dayLabels[index], ForecastDayFormatter.label(day.date, Locale.FRENCH).uppercase(Locale.FRENCH))
                    setImageViewResource(dayIcons[index], HtcWeatherDrawable.forCode(dayVisual.iconCode))
                    setTextViewText(dayTemperatures[index], "${day.maximum}° ${day.minimum}°")
                }
            }

            val openIntent = Intent(context, MainActivity::class.java).putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId)
            setOnClickPendingIntent(R.id.widget_root, PendingIntent.getActivity(context, appWidgetId, openIntent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE))
            val refreshIntent = Intent(context, WidgetRefreshReceiver::class.java).putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId)
            setOnClickPendingIntent(R.id.weather_panel, PendingIntent.getBroadcast(context, appWidgetId, refreshIntent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE))
        }
    }
}
