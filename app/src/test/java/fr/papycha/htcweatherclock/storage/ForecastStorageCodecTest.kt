package fr.papycha.htcweatherclock.storage

import fr.papycha.htcweatherclock.weather.DailyForecast
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class ForecastStorageCodecTest {
    @Test fun sevenDaysRoundTripWithoutLosingValues() {
        val days = listOf(
            DailyForecast("2026-10-03", 12, 21, 2),
            DailyForecast("2026-10-04", 10, 19, 61)
        )
        assertEquals(days, ForecastStorageCodec.decode(ForecastStorageCodec.encode(days)))
    }

    @Test fun malformedEntriesAreIgnored() {
        val decoded = ForecastStorageCodec.decode("bad;2026-10-03,12,21,2;also-bad")
        assertEquals(listOf(DailyForecast("2026-10-03", 12, 21, 2)), decoded)
        assertTrue(ForecastStorageCodec.decode("").isEmpty())
    }
}
