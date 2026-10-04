package fr.papycha.htcweatherclock.weather

import java.net.URLEncoder

data class ResolvedLocation(
    val label: String,
    val latitude: Double,
    val longitude: Double,
    val weatherLabel: String = label
)

object GeocodingSpec {
    private const val NOMINATIM = "https://nominatim.openstreetmap.org"
    private const val FORECAST = "https://api.open-meteo.com/v1/forecast"

    fun searchUrl(address: String): String =
        "$NOMINATIM/search?format=jsonv2&limit=1&addressdetails=1&accept-language=fr&q=${URLEncoder.encode(address.trim(), "UTF-8")}" 

    fun reverseUrl(latitude: Double, longitude: Double): String =
        "$NOMINATIM/reverse?format=jsonv2&addressdetails=1&accept-language=fr&lat=$latitude&lon=$longitude"

    fun forecastUrl(latitude: Double, longitude: Double): String =
        "$FORECAST?latitude=$latitude&longitude=$longitude&current=temperature_2m,weather_code&daily=weather_code,temperature_2m_max,temperature_2m_min&timezone=auto&forecast_days=7"

    fun parseFirst(json: String): ResolvedLocation? {
        val latitude = stringField(json, "lat")?.toDoubleOrNull() ?: return null
        val longitude = stringField(json, "lon")?.toDoubleOrNull() ?: return null
        val label = stringField(json, "display_name")?.takeIf { it.isNotBlank() } ?: return null
        val weatherLabel = listOf("city", "town", "village", "municipality")
            .firstNotNullOfOrNull { stringField(json, it)?.takeIf(String::isNotBlank) }
            ?: label
        return ResolvedLocation(label, latitude, longitude, weatherLabel)
    }

    private fun stringField(json: String, name: String): String? =
        Regex("\\\"$name\\\"\\s*:\\s*\\\"((?:\\\\.|[^\\\"])*)\\\"")
            .find(json)?.groupValues?.get(1)
            ?.replace("\\u0026", "&")
            ?.replace("\\/", "/")
            ?.replace("\\\"", "\"")
            ?.replace("\\\\", "\\")
}
