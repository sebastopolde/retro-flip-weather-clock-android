package fr.papycha.htcweatherclock.weather

data class WeatherSnapshot(val city: String, val temperature: Int, val minimum: Int, val maximum: Int, val weatherCode: Int, val updatedAt: Long) {
    val symbol: String get() = when (weatherCode) {
        0 -> "☀"; 1, 2 -> "🌤"; 3 -> "☁"; in 45..48 -> "🌫"; in 51..67, in 80..82 -> "🌧"; in 71..77, in 85..86 -> "❄"; in 95..99 -> "⛈"; else -> "☁"
    }
}
data class WeatherResult(val snapshot: WeatherSnapshot?, val fresh: Boolean, val retryable: Boolean = false)
