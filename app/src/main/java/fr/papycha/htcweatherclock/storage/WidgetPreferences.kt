package fr.papycha.htcweatherclock.storage

import android.content.Context
import fr.papycha.htcweatherclock.weather.WeatherSnapshot

object WidgetPreferences {
    private fun prefs(context: Context) = context.getSharedPreferences("widget_state", Context.MODE_PRIVATE)
    fun city(context: Context, widgetId: Int = 0): String = prefs(context).getString("city_$widgetId", prefs(context).getString("city_0", "Paris")) ?: "Paris"
    fun setCity(context: Context, city: String, widgetId: Int = 0) { prefs(context).edit().putString("city_$widgetId", city.trim().ifEmpty { "Paris" }).apply() }
    fun weather(context: Context, widgetId: Int = 0): WeatherSnapshot? {
        return WeatherStorageCodec.decode(prefs(context).all, widgetId)
    }
    fun setWeather(context: Context, s: WeatherSnapshot, widgetId: Int = 0) {
        val p = "weather_$widgetId"; prefs(context).edit().putString("${p}_city", s.city).putInt("${p}_temp", s.temperature).putInt("${p}_min", s.minimum).putInt("${p}_max", s.maximum).putInt("${p}_code", s.weatherCode).putLong("${p}_time", s.updatedAt).putString("${p}_forecast", ForecastStorageCodec.encode(s.forecast)).apply()
    }
    fun delete(context: Context, widgetId: Int) {
        val e = prefs(context).edit(); listOf("city_$widgetId", "weather_${widgetId}_city", "weather_${widgetId}_temp", "weather_${widgetId}_min", "weather_${widgetId}_max", "weather_${widgetId}_code", "weather_${widgetId}_time", "weather_${widgetId}_forecast").forEach(e::remove); e.apply()
    }
}
