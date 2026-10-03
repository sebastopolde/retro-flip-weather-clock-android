package fr.papycha.htcweatherclock.clock

import java.util.Calendar
import java.util.Locale
import java.util.TimeZone
import org.junit.Assert.assertEquals
import org.junit.Test

class ClockFormatterTest {
    private fun at(hour: Int, minute: Int) = Calendar.getInstance(TimeZone.getTimeZone("UTC")).apply {
        set(2026, Calendar.OCTOBER, 3, hour, minute, 0)
        set(Calendar.MILLISECOND, 0)
    }.timeInMillis

    @Test fun formatsMidnightAndNoonInTwelveHourMode() {
        assertEquals("12:05", ClockFormatter.time(at(0, 5), false, Locale.US, TimeZone.getTimeZone("UTC")))
        assertEquals("12:05", ClockFormatter.time(at(12, 5), false, Locale.US, TimeZone.getTimeZone("UTC")))
    }

    @Test fun formatsTwentyFourHourTimeWithoutAmPm() {
        assertEquals("00:05", ClockFormatter.time(at(0, 5), true, Locale.FRANCE, TimeZone.getTimeZone("UTC")))
        assertEquals("23:07", ClockFormatter.time(at(23, 7), true, Locale.FRANCE, TimeZone.getTimeZone("UTC")))
    }

    @Test fun formatsFrenchDateFromTheRequestedLocale() {
        assertEquals("sam. 3 oct.", ClockFormatter.date(at(16, 0), Locale.FRANCE, TimeZone.getTimeZone("UTC")))
    }
}
