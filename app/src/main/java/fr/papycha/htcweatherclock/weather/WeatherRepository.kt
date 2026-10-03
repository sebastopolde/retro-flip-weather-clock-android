package fr.papycha.htcweatherclock.weather

import kotlin.math.roundToInt

object WeatherRepository {
    private fun number(json: String, key: String): Double? = Regex("\\\"$key\\\"\\s*:\\s*(?:\\[\\s*)?(-?\\d+(?:\\.\\d+)?)").find(json)?.groupValues?.get(1)?.toDoubleOrNull()
    fun resolve(json: String, previous: WeatherSnapshot?, now: Long, city: String = "Paris"): WeatherResult {
        val current = number(json, "temperature_2m"); val code = number(json, "weather_code")
        val max = number(json, "temperature_2m_max"); val min = number(json, "temperature_2m_min")
        if (current == null || code == null || max == null || min == null) return WeatherResult(previous, false)
        return WeatherResult(WeatherSnapshot(city, current.roundToInt(), min.roundToInt(), max.roundToInt(), code.roundToInt(), now), true)
    }
}
