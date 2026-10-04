package fr.papycha.htcweatherclock.widget

import android.appwidget.AppWidgetManager
import android.content.Context
import android.os.Build
import android.widget.RemoteViews
import fr.papycha.htcweatherclock.R
import fr.papycha.htcweatherclock.storage.WidgetPreferences
import fr.papycha.htcweatherclock.weather.HtcWeatherPresentation
import fr.papycha.htcweatherclock.weather.HtcWeatherDrawable

object WidgetRenderer {
    fun render(context: Context, appWidgetId: Int): RemoteViews {
        val manager = AppWidgetManager.getInstance(context)
        val providerName = manager.getAppWidgetInfo(appWidgetId)?.provider?.className
        if (WidgetKindSelector.fromProvider(providerName) == WidgetKind.WEEKLY) return WeeklyWidgetRenderer.render(context, appWidgetId)
        val width = manager.getAppWidgetOptions(appWidgetId).getInt(AppWidgetManager.OPTION_APPWIDGET_MIN_WIDTH, 250)
        val layout = when (WidgetLayoutSelector.select(width, Build.VERSION.SDK_INT)) { WidgetLayout.COMPACT -> R.layout.widget_compact; WidgetLayout.CLASSIC -> R.layout.widget_classic; WidgetLayout.WIDE -> R.layout.widget_wide }
        val weather = WidgetPreferences.weather(context, appWidgetId)
        val location = WidgetPreferences.location(context, appWidgetId)
        return RemoteViews(context.packageName, layout).apply {
            val visual = weather?.let { HtcWeatherPresentation.forWmo(it.weatherCode) }
            setImageViewResource(R.id.weather_icon, HtcWeatherDrawable.forCode(visual?.iconCode ?: 7))
            setTextViewText(R.id.temperature, weather?.let { "${it.temperature}°" } ?: "--°")
            setTextViewText(R.id.city, weather?.city ?: context.getString(R.string.tap_to_configure))
            setTextViewText(R.id.condition, visual?.label ?: context.getString(R.string.weather_offline))
            setTextViewText(R.id.range, weather?.let { "${it.maximum}° / ${it.minimum}°" } ?: "--° / --°")
            setOnClickPendingIntent(R.id.widget_root, WidgetActionActivity.pendingIntent(context, appWidgetId, WidgetAction.ALARMS))
            val weatherCity = WidgetActionSpec.weatherCity(WidgetPreferences.city(context, appWidgetId), weather?.city)
            setOnClickPendingIntent(R.id.weather_panel, WidgetActionActivity.pendingIntent(context, appWidgetId, WidgetAction.WEATHER_DETAILS, weatherCity, location))
        }
    }
}
