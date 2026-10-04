package fr.papycha.htcweatherclock.storage

import android.content.Context
import fr.papycha.htcweatherclock.weather.WeatherSnapshot
import fr.papycha.htcweatherclock.weather.ResolvedLocation

object WidgetPreferences {
    private fun prefs(context: Context) = context.getSharedPreferences("widget_state", Context.MODE_PRIVATE)
    fun city(context: Context, widgetId: Int = 0): String = prefs(context).getString("city_$widgetId", prefs(context).getString("city_0", "Paris")) ?: "Paris"
    fun setCity(context: Context, city: String, widgetId: Int = 0) {
        val prefix = "location_$widgetId"
        prefs(context).edit().putString("city_$widgetId", city.trim().ifEmpty { "Paris" })
            .remove("${prefix}_label").remove("${prefix}_latitude").remove("${prefix}_longitude")
            .remove("${prefix}_weather_label").apply()
    }
    fun location(context: Context, widgetId: Int = 0): ResolvedLocation? =
        LocationStorageCodec.decode(prefs(context).all, widgetId)
    fun setLocation(context: Context, location: ResolvedLocation, widgetId: Int = 0) {
        val prefix = "location_$widgetId"
        prefs(context).edit().putString("city_$widgetId", location.label)
            .putString("${prefix}_label", location.label)
            .putString("${prefix}_latitude", location.latitude.toString())
            .putString("${prefix}_longitude", location.longitude.toString())
            .putString("${prefix}_weather_label", location.weatherLabel).apply()
    }
    fun weather(context: Context, widgetId: Int = 0): WeatherSnapshot? {
        return WeatherStorageCodec.decode(prefs(context).all, widgetId)
    }
    fun setWeather(context: Context, s: WeatherSnapshot, widgetId: Int = 0) {
        val p = "weather_$widgetId"; prefs(context).edit().putString("${p}_city", s.city).putInt("${p}_temp", s.temperature).putInt("${p}_min", s.minimum).putInt("${p}_max", s.maximum).putInt("${p}_code", s.weatherCode).putLong("${p}_time", s.updatedAt).putString("${p}_forecast", ForecastStorageCodec.encode(s.forecast)).apply()
    }
    fun copyGlobalConfiguration(context: Context, targetWidgetId: Int) {
        val editor = prefs(context).edit()
        WidgetStateTransfer.fromGlobal(prefs(context).all, targetWidgetId).forEach { (key, value) ->
            when (value) {
                is String -> editor.putString(key, value)
                is Int -> editor.putInt(key, value)
                is Long -> editor.putLong(key, value)
                is Float -> editor.putFloat(key, value)
                is Boolean -> editor.putBoolean(key, value)
            }
        }
        editor.apply()
    }
    fun delete(context: Context, widgetId: Int) {
        val e = prefs(context).edit(); listOf("city_$widgetId", "location_${widgetId}_label", "location_${widgetId}_latitude", "location_${widgetId}_longitude", "location_${widgetId}_weather_label", "weather_${widgetId}_city", "weather_${widgetId}_temp", "weather_${widgetId}_min", "weather_${widgetId}_max", "weather_${widgetId}_code", "weather_${widgetId}_time", "weather_${widgetId}_forecast").forEach(e::remove); e.apply()
    }
}
