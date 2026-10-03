package fr.papycha.htcweatherclock.weather

import java.text.SimpleDateFormat
import java.util.Locale

object ForecastDayFormatter {
    fun label(date: String, locale: Locale = Locale.getDefault()): String = try {
        val parser = SimpleDateFormat("yyyy-MM-dd", Locale.ROOT).apply { isLenient = false }
        val formatter = SimpleDateFormat("EEE", locale)
        val parsed = parser.parse(date) ?: throw IllegalArgumentException("Invalid date")
        formatter.format(parsed)
    } catch (_: Exception) {
        "—"
    }
}
