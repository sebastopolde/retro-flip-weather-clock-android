package fr.papycha.htcweatherclock.storage

import fr.papycha.htcweatherclock.weather.ResolvedLocation
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class LocationStorageCodecTest {
    @Test fun completeValidatedLocationCanBeRestoredForBackgroundRefresh() {
        val values = mapOf<String, Any>(
            "location_8_label" to "Saint-Maurice-Montcouronne, France",
            "location_8_latitude" to "48.5821",
            "location_8_longitude" to "2.1245"
        )

        assertEquals(
            ResolvedLocation("Saint-Maurice-Montcouronne, France", 48.5821, 2.1245),
            LocationStorageCodec.decode(values, 8)
        )
    }

    @Test fun incompleteCoordinatesAreNeverUsed() {
        assertNull(LocationStorageCodec.decode(mapOf("location_8_label" to "Adresse"), 8))
    }
}
