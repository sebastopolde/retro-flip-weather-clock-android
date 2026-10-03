package fr.papycha.htcweatherclock.weather

import kotlin.math.roundToInt

object WeatherRepository {
    private fun number(json: String, key: String): Double? = Regex("\\\"$key\\\"\\s*:\\s*(?:\\[\\s*)?(-?\\d+(?:\\.\\d+)?)").find(json)?.groupValues?.get(1)?.toDoubleOrNull()
    private fun arrayBody(json: String, key: String): String? = Regex("\\\"$key\\\"\\s*:\\s*\\[([^]]*)]").find(json)?.groupValues?.get(1)
    private fun numberArray(json: String, key: String): List<Double> = arrayBody(json, key)?.split(',')?.mapNotNull { it.trim().toDoubleOrNull() } ?: emptyList()
    private fun stringArray(json: String, key: String): List<String> = arrayBody(json, key)?.let { Regex("\\\"([^\\\"]+)\\\"").findAll(it).map { match -> match.groupValues[1] }.toList() } ?: emptyList()
    fun resolve(json: String, previous: WeatherSnapshot?, now: Long, city: String = "Paris"): WeatherResult {
        val current = number(json, "temperature_2m"); val code = number(json, "weather_code")
        val max = number(json, "temperature_2m_max"); val min = number(json, "temperature_2m_min")
        if (current == null || code == null || max == null || min == null) return WeatherResult(previous, false)
        val dates = stringArray(json, "time")
        val dailyCodes = numberArray(json, "weather_code")
        val maxima = numberArray(json, "temperature_2m_max")
        val minima = numberArray(json, "temperature_2m_min")
        val count = minOf(7, dates.size, dailyCodes.size, maxima.size, minima.size)
        val forecast = (0 until count).map { index -> DailyForecast(dates[index], minima[index].roundToInt(), maxima[index].roundToInt(), dailyCodes[index].roundToInt()) }
        return WeatherResult(WeatherSnapshot(city, current.roundToInt(), min.roundToInt(), max.roundToInt(), code.roundToInt(), now, forecast), true)
    }
}
