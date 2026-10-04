package fr.papycha.htcweatherclock.widget

import android.appwidget.AppWidgetManager
import android.content.Context
import android.view.View
import android.widget.RemoteViews
import fr.papycha.htcweatherclock.R
import fr.papycha.htcweatherclock.storage.WidgetPreferences
import fr.papycha.htcweatherclock.weather.ForecastDayFormatter
import fr.papycha.htcweatherclock.weather.HtcWeatherDrawable
import fr.papycha.htcweatherclock.weather.HtcWeatherPresentation
import fr.papycha.htcweatherclock.weather.WeeklyWeatherFormatter
import java.util.Locale

object WeeklyWidgetRenderer {
    private val dayLabels = intArrayOf(R.id.day_1_label, R.id.day_2_label, R.id.day_3_label, R.id.day_4_label)
    private val dayIcons = intArrayOf(R.id.day_1_icon, R.id.day_2_icon, R.id.day_3_icon, R.id.day_4_icon)
    private val dayTemperatures = intArrayOf(R.id.day_1_temperature, R.id.day_2_temperature, R.id.day_3_temperature, R.id.day_4_temperature)
    private val dayColumns = intArrayOf(R.id.day_1, R.id.day_2, R.id.day_3, R.id.day_4)

    fun render(context: Context, appWidgetId: Int): RemoteViews {
        val weather = WidgetPreferences.weather(context, appWidgetId)
        val location = WidgetPreferences.location(context, appWidgetId)
        val visual = weather?.let { HtcWeatherPresentation.forWmo(it.weatherCode) }
        val forecast = WeeklyWeatherFormatter.visibleForecast(weather?.forecast.orEmpty())
        return RemoteViews(context.packageName, R.layout.widget_weekly).apply {
            setImageViewResource(R.id.weather_icon, HtcWeatherDrawable.forCode(visual?.iconCode ?: 7))
            setTextViewText(R.id.temperature, weather?.let { WeeklyWeatherFormatter.currentTemperature(it.temperature) } ?: "--°C")
            setTextViewText(R.id.city, weather?.city ?: context.getString(R.string.tap_to_configure))
            setTextViewText(R.id.condition, visual?.label ?: context.getString(R.string.weather_offline))
            setTextViewText(R.id.range, weather?.let { WeeklyWeatherFormatter.currentRange(it.minimum, it.maximum) } ?: "--° / --°")
            setTextViewText(R.id.widget_date, WeeklyWeatherFormatter.date(System.currentTimeMillis()))

            dayColumns.indices.forEach { index ->
                val day = forecast.getOrNull(index)
                setViewVisibility(dayColumns[index], if (day == null) View.INVISIBLE else View.VISIBLE)
                if (day != null) {
                    val dayVisual = HtcWeatherPresentation.forWmo(day.weatherCode)
                    setTextViewText(dayLabels[index], ForecastDayFormatter.label(day.date, Locale.FRENCH))
                    setImageViewResource(dayIcons[index], HtcWeatherDrawable.forCode(dayVisual.iconCode))
                    setTextViewText(dayTemperatures[index], WeeklyWeatherFormatter.forecastRange(day.minimum, day.maximum))
                }
            }

            setOnClickPendingIntent(R.id.widget_root, WidgetActionActivity.pendingIntent(context, appWidgetId, WidgetAction.ALARMS))
            val weatherCity = WidgetActionSpec.weatherCity(WidgetPreferences.city(context, appWidgetId), weather?.city)
            val weatherPendingIntent = WidgetActionActivity.pendingIntent(context, appWidgetId, WidgetAction.WEATHER_DETAILS, weatherCity, location)
            setOnClickPendingIntent(R.id.current_weather_panel, weatherPendingIntent)
            setOnClickPendingIntent(R.id.forecast_panel, weatherPendingIntent)
        }
    }
}
