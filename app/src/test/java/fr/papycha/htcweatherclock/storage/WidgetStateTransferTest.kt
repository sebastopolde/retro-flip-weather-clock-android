package fr.papycha.htcweatherclock.storage

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Test

class WidgetStateTransferTest {
    @Test fun newlyPinnedWidgetReceivesValidatedGlobalLocationAndWeather() {
        val source = mapOf<String, Any>(
            "city_0" to "Adresse complète",
            "location_0_label" to "Adresse complète",
            "location_0_latitude" to "48.8566",
            "location_0_longitude" to "2.3522",
            "location_0_weather_label" to "Paris",
            "weather_0_city" to "Paris",
            "weather_0_temp" to 15,
            "weather_0_time" to 123L,
            "unrelated" to "ignored"
        )

        val transferred = WidgetStateTransfer.fromGlobal(source, targetWidgetId = 42)

        assertEquals("Adresse complète", transferred["city_42"])
        assertEquals("48.8566", transferred["location_42_latitude"])
        assertEquals("Paris", transferred["weather_42_city"])
        assertEquals(15, transferred["weather_42_temp"])
        assertEquals(123L, transferred["weather_42_time"])
        assertFalse(transferred.containsKey("unrelated"))
    }
}
