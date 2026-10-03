package fr.papycha.htcweatherclock.weather

import org.junit.Assert.assertEquals
import org.junit.Test
import java.util.Locale

class ForecastDayFormatterTest {
    @Test fun isoDateBecomesShortFrenchDayName() {
        assertEquals("sam.", ForecastDayFormatter.label("2026-10-03", Locale.FRENCH))
    }

    @Test fun invalidDateUsesFallbackMarker() {
        assertEquals("—", ForecastDayFormatter.label("invalid", Locale.FRENCH))
    }
}
