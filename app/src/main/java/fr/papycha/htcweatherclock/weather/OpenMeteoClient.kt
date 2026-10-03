package fr.papycha.htcweatherclock.weather

import java.net.HttpURLConnection
import java.net.URL
import java.net.URLEncoder

object OpenMeteoClient {
    private fun get(url: String): String = (URL(url).openConnection() as HttpURLConnection).run {
        connectTimeout = 8_000; readTimeout = 8_000; setRequestProperty("User-Agent", "HTCWeatherClockModern/0.1")
        inputStream.bufferedReader().use { it.readText() }
    }
    fun fetch(city: String, previous: WeatherSnapshot?): WeatherResult = try {
        val wanted = city.trim().ifEmpty { "Paris" }
        val geo = get("https://geocoding-api.open-meteo.com/v1/search?name=${URLEncoder.encode(wanted, "UTF-8")}&count=1&language=fr&format=json")
        val latitude = Regex("\\\"latitude\\\"\\s*:\\s*(-?\\d+(?:\\.\\d+)?)").find(geo)?.groupValues?.get(1)
        val longitude = Regex("\\\"longitude\\\"\\s*:\\s*(-?\\d+(?:\\.\\d+)?)").find(geo)?.groupValues?.get(1)
        val name = Regex("\\\"name\\\"\\s*:\\s*\\\"([^\\\"]+)\\\"").find(geo)?.groupValues?.get(1) ?: wanted
        if (latitude == null || longitude == null) WeatherResult(previous, false) else {
            val json = get("https://api.open-meteo.com/v1/forecast?latitude=$latitude&longitude=$longitude&current=temperature_2m,weather_code&daily=temperature_2m_max,temperature_2m_min&timezone=auto&forecast_days=1")
            WeatherRepository.resolve(json, previous, System.currentTimeMillis(), name)
        }
    } catch (_: java.io.IOException) { WeatherResult(previous, fresh = false, retryable = true) }
      catch (_: Exception) { WeatherResult(previous, fresh = false, retryable = false) }
}
