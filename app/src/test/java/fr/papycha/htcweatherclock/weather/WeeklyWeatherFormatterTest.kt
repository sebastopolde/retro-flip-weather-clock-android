package fr.papycha.htcweatherclock.weather

import org.junit.Assert.assertEquals
import org.junit.Test
import java.util.Calendar
import java.util.GregorianCalendar
import java.util.Locale
import java.util.TimeZone

class WeeklyWeatherFormatterTest {
    @Test fun currentTemperatureIncludesCelsiusUnit() {
        assertEquals("18°C", WeeklyWeatherFormatter.currentTemperature(18))
    }

    @Test fun currentRangeShowsMaximumBeforeMinimum() {
        assertEquals("23° / 11°", WeeklyWeatherFormatter.currentRange(minimum = 11, maximum = 23))
    }

    @Test fun forecastRangeShowsMinimumBeforeMaximum() {
        assertEquals("12° / 22°", WeeklyWeatherFormatter.forecastRange(minimum = 12, maximum = 22))
    }

    @Test fun dateIsAlwaysRenderedInCompactFrench() {
        val utc = TimeZone.getTimeZone("UTC")
        val instant = GregorianCalendar(utc, Locale.FRENCH).apply {
            set(2026, Calendar.OCTOBER, 3, 12, 0, 0)
            set(Calendar.MILLISECOND, 0)
        }.timeInMillis

        assertEquals("sam. 3 oct", WeeklyWeatherFormatter.date(instant, utc))
    }

    @Test fun forecastStartsTomorrowAndShowsFourDays() {
        val days = (3..8).map { day -> DailyForecast("2026-10-${day.toString().padStart(2, '0')}", 10, 20, 2) }
        val utc = TimeZone.getTimeZone("UTC")
        val now = GregorianCalendar(utc, Locale.FRENCH).apply {
            set(2026, Calendar.OCTOBER, 3, 12, 0, 0)
            set(Calendar.MILLISECOND, 0)
        }.timeInMillis

        assertEquals(
            listOf(
                DailyForecast("2026-10-04", 10, 20, 2),
                DailyForecast("2026-10-05", 10, 20, 2),
                DailyForecast("2026-10-06", 10, 20, 2),
                DailyForecast("2026-10-07", 10, 20, 2)
            ),
            WeeklyWeatherFormatter.visibleForecast(days, now, utc)
        )
    }

    @Test fun stalePastDaysAreDiscardedByDate() {
        val utc = TimeZone.getTimeZone("UTC")
        val now = GregorianCalendar(utc, Locale.FRENCH).apply {
            set(2026, Calendar.OCTOBER, 3, 23, 30, 0)
            set(Calendar.MILLISECOND, 0)
        }.timeInMillis
        val days = (1..7).map { day -> DailyForecast("2026-10-${day.toString().padStart(2, '0')}", 10, 20, 2) }

        assertEquals(listOf("2026-10-04", "2026-10-05", "2026-10-06", "2026-10-07"), WeeklyWeatherFormatter.visibleForecast(days, now, utc).map { it.date })
    }

    @Test fun listAlreadyStartingTomorrowKeepsItsFirstDay() {
        val utc = TimeZone.getTimeZone("UTC")
        val now = GregorianCalendar(utc, Locale.FRENCH).apply {
            set(2026, Calendar.OCTOBER, 3, 12, 0, 0)
            set(Calendar.MILLISECOND, 0)
        }.timeInMillis
        val days = (4..8).map { day -> DailyForecast("2026-10-${day.toString().padStart(2, '0')}", 10, 20, 2) }

        assertEquals(listOf("2026-10-04", "2026-10-05", "2026-10-06", "2026-10-07"), WeeklyWeatherFormatter.visibleForecast(days, now, utc).map { it.date })
    }
}
