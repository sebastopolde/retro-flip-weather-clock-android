package fr.papycha.htcweatherclock.weather

import java.net.HttpURLConnection
import java.net.URL
import java.net.URLEncoder

object OpenMeteoClient {
    private fun get(url: String): String = (URL(url).openConnection() as HttpURLConnection).run {
        connectTimeout = 8_000; readTimeout = 8_000; setRequestProperty("User-Agent", "HTCWeatherClockModern/0.1")
        val status = responseCode
        if (status !in 200..299) {
            errorStream?.close()
            disconnect()
            throw RemoteServiceException(status)
        }
        inputStream.bufferedReader().use { it.readText() }
    }
    fun resolveAddress(address: String): AddressResolution = AddressResolver.resolve(address, ::get)

    fun reverse(latitude: Double, longitude: Double): ResolvedLocation = try {
        GeocodingSpec.parseFirst(get(GeocodingSpec.reverseUrl(latitude, longitude)))
            ?: ResolvedLocation(coordinateLabel(latitude, longitude), latitude, longitude)
    } catch (_: Exception) {
        ResolvedLocation(coordinateLabel(latitude, longitude), latitude, longitude)
    }

    fun fetch(location: ResolvedLocation, previous: WeatherSnapshot?): WeatherResult = try {
        val json = get(GeocodingSpec.forecastUrl(location.latitude, location.longitude))
        WeatherRepository.resolve(json, previous, System.currentTimeMillis(), location.weatherLabel)
    } catch (_: java.io.IOException) { WeatherResult(previous, fresh = false, retryable = true) }
      catch (_: Exception) { WeatherResult(previous, fresh = false, retryable = false) }

    fun fetch(city: String, previous: WeatherSnapshot?): WeatherResult {
        return try {
            val wanted = city.trim().ifEmpty { "Paris" }
            val json = get("https://geocoding-api.open-meteo.com/v1/search?name=${URLEncoder.encode(wanted, "UTF-8")}&count=1&language=fr&format=json")
            val latitude = Regex("\\\"latitude\\\"\\s*:\\s*(-?\\d+(?:\\.\\d+)?)").find(json)?.groupValues?.get(1)?.toDoubleOrNull()
            val longitude = Regex("\\\"longitude\\\"\\s*:\\s*(-?\\d+(?:\\.\\d+)?)").find(json)?.groupValues?.get(1)?.toDoubleOrNull()
            val name = Regex("\\\"name\\\"\\s*:\\s*\\\"([^\\\"]+)\\\"").find(json)?.groupValues?.get(1) ?: wanted
            if (latitude == null || longitude == null) WeatherResult(previous, fresh = false)
            else fetch(ResolvedLocation(name, latitude, longitude), previous)
        } catch (_: java.io.IOException) { WeatherResult(previous, fresh = false, retryable = true) }
          catch (_: Exception) { WeatherResult(previous, fresh = false) }
    }

    private fun coordinateLabel(latitude: Double, longitude: Double) =
        "Ma position (${String.format(java.util.Locale.US, "%.5f, %.5f", latitude, longitude)})"
}
