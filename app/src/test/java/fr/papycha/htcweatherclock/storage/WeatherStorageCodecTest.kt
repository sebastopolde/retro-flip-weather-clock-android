package fr.papycha.htcweatherclock.storage

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class WeatherStorageCodecTest {
    private val global = mapOf<String, Any>("weather_0_city" to "Paris", "weather_0_temp" to 20, "weather_0_min" to 12, "weather_0_max" to 22, "weather_0_code" to 1, "weather_0_time" to 10L)

    @Test fun widgetWithoutOwnCacheNeverReadsGlobalWeather() { assertNull(WeatherStorageCodec.decode(global, 42)) }

    @Test fun widgetReadsOnlyItsOwnCompleteSnapshot() {
        val values = global + mapOf<String, Any>("weather_42_city" to "Lyon", "weather_42_temp" to 17, "weather_42_min" to 9, "weather_42_max" to 18, "weather_42_code" to 3, "weather_42_time" to 20L)
        assertEquals("Lyon", WeatherStorageCodec.decode(values, 42)?.city)
    }
}
