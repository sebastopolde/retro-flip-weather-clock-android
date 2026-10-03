package fr.papycha.htcweatherclock.weather

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Test

class WeatherRepositoryTest {
    @Test fun malformedResponseKeepsLastValidSnapshot() {
        val previous = WeatherSnapshot("Paris", 18, 12, 21, 2, 10L)
        val result = WeatherRepository.resolve("not-json", previous, 20L)
        assertEquals(previous, result.snapshot)
        assertFalse(result.fresh)
    }

    @Test fun emptyResponseWithoutCacheReturnsNoWeather() {
        val result = WeatherRepository.resolve("", null, 20L)
        assertNull(result.snapshot)
        assertFalse(result.fresh)
    }

    @Test fun validOpenMeteoResponseProducesSnapshot() {
        val json = """{"current":{"temperature_2m":18.4,"weather_code":2},"daily":{"temperature_2m_max":[21.1],"temperature_2m_min":[11.8]}}"""
        val result = WeatherRepository.resolve(json, null, 99L, "Paris")
        assertEquals(WeatherSnapshot("Paris", 18, 12, 21, 2, 99L), result.snapshot)
        assertEquals(true, result.fresh)
    }
}
