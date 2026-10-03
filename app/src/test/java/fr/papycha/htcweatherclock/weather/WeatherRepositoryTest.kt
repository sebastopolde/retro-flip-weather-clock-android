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

    @Test fun sevenDayArraysProduceACompleteWeeklyForecast() {
        val json = """{"current":{"temperature_2m":18.4,"weather_code":2},"daily":{"time":["2026-10-03","2026-10-04","2026-10-05","2026-10-06","2026-10-07","2026-10-08","2026-10-09"],"weather_code":[2,3,61,0,45,71,95],"temperature_2m_max":[21.1,20,18,22,17,9,16],"temperature_2m_min":[11.8,10,9,12,8,1,7]}}"""
        val forecast = WeatherRepository.resolve(json, null, 99L, "Paris").snapshot!!.forecast
        assertEquals(7, forecast.size)
        assertEquals(DailyForecast("2026-10-03", 12, 21, 2), forecast.first())
        assertEquals(DailyForecast("2026-10-09", 7, 16, 95), forecast.last())
    }
}
