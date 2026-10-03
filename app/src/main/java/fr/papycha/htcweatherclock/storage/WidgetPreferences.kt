package fr.papycha.htcweatherclock.storage

import android.content.Context
import fr.papycha.htcweatherclock.weather.WeatherSnapshot

object WidgetPreferences {
    private fun prefs(context: Context) = context.getSharedPreferences("widget_state", Context.MODE_PRIVATE)
    fun city(context: Context, widgetId: Int = 0): String = prefs(context).getString("city_$widgetId", prefs(context).getString("city_0", "Paris")) ?: "Paris"
    fun setCity(context: Context, city: String, widgetId: Int = 0) { prefs(context).edit().putString("city_$widgetId", city.trim().ifEmpty { "Paris" }).apply() }
    fun weather(context: Context, widgetId: Int = 0): WeatherSnapshot? {
        val p = prefs(context); val prefix = "weather_$widgetId"
        if (!p.contains("${prefix}_temp")) return if (widgetId == 0) null else weather(context, 0)
        return WeatherSnapshot(p.getString("${prefix}_city", "Paris") ?: "Paris", p.getInt("${prefix}_temp", 0), p.getInt("${prefix}_min", 0), p.getInt("${prefix}_max", 0), p.getInt("${prefix}_code", 3), p.getLong("${prefix}_time", 0L))
    }
    fun setWeather(context: Context, s: WeatherSnapshot, widgetId: Int = 0) {
        val p = "weather_$widgetId"; prefs(context).edit().putString("${p}_city", s.city).putInt("${p}_temp", s.temperature).putInt("${p}_min", s.minimum).putInt("${p}_max", s.maximum).putInt("${p}_code", s.weatherCode).putLong("${p}_time", s.updatedAt).apply()
    }
    fun delete(context: Context, widgetId: Int) {
        val e = prefs(context).edit(); listOf("city_$widgetId", "weather_${widgetId}_city", "weather_${widgetId}_temp", "weather_${widgetId}_min", "weather_${widgetId}_max", "weather_${widgetId}_code", "weather_${widgetId}_time").forEach(e::remove); e.apply()
    }
}
