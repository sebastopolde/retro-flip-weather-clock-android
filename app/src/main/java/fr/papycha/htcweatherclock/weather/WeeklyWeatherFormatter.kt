package fr.papycha.htcweatherclock.weather

import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import java.util.TimeZone

object WeeklyWeatherFormatter {
    fun currentTemperature(temperature: Int): String = "${temperature}°C"

    fun currentRange(minimum: Int, maximum: Int): String = "${maximum}° / ${minimum}°"

    fun forecastRange(minimum: Int, maximum: Int): String = "${minimum}° / ${maximum}°"

    fun date(epochMillis: Long, timeZone: TimeZone = TimeZone.getDefault()): String =
        SimpleDateFormat("EEE d MMM", Locale.FRENCH).apply { this.timeZone = timeZone }
            .format(Date(epochMillis))
            .removeSuffix(".")

    fun visibleForecast(
        days: List<DailyForecast>,
        now: Long = System.currentTimeMillis(),
        timeZone: TimeZone = TimeZone.getDefault()
    ): List<DailyForecast> {
        val today = Calendar.getInstance(timeZone).apply {
            timeInMillis = now
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }.timeInMillis
        val parser = SimpleDateFormat("yyyy-MM-dd", Locale.US).apply {
            this.timeZone = timeZone
            isLenient = false
        }
        return days.mapNotNull { day ->
            val timestamp = runCatching { parser.parse(day.date)?.time }.getOrNull()
            if (timestamp != null && timestamp > today) timestamp to day else null
        }.sortedBy { it.first }.take(4).map { it.second }
    }
}
