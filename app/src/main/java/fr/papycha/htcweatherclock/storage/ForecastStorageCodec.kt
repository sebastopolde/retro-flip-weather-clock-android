package fr.papycha.htcweatherclock.storage

import fr.papycha.htcweatherclock.weather.DailyForecast

object ForecastStorageCodec {
    fun encode(days: List<DailyForecast>): String = days.joinToString(";") { "${it.date},${it.minimum},${it.maximum},${it.weatherCode}" }

    fun decode(value: String?): List<DailyForecast> = value.orEmpty().split(';').mapNotNull { entry ->
        val parts = entry.split(',')
        if (parts.size != 4) return@mapNotNull null
        val minimum = parts[1].toIntOrNull() ?: return@mapNotNull null
        val maximum = parts[2].toIntOrNull() ?: return@mapNotNull null
        val weatherCode = parts[3].toIntOrNull() ?: return@mapNotNull null
        DailyForecast(parts[0], minimum, maximum, weatherCode)
    }
}
