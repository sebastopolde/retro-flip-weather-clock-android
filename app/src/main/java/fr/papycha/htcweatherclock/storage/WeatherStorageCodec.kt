package fr.papycha.htcweatherclock.storage

import fr.papycha.htcweatherclock.weather.WeatherSnapshot

object WeatherStorageCodec {
    fun decode(values: Map<String, *>, widgetId: Int): WeatherSnapshot? {
        val prefix = "weather_$widgetId"
        if (!values.containsKey("${prefix}_temp")) return null
        return WeatherSnapshot(
            values["${prefix}_city"] as? String ?: return null,
            values["${prefix}_temp"] as? Int ?: return null,
            values["${prefix}_min"] as? Int ?: return null,
            values["${prefix}_max"] as? Int ?: return null,
            values["${prefix}_code"] as? Int ?: return null,
            values["${prefix}_time"] as? Long ?: return null,
            ForecastStorageCodec.decode(values["${prefix}_forecast"] as? String)
        )
    }
}
