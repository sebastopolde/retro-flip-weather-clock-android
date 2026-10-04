package fr.papycha.htcweatherclock.location

import org.junit.Assert.assertEquals
import org.junit.Test

class LocationProviderSpecTest {
    @Test fun approximatePermissionNeverRequestsGpsProvider() {
        assertEquals(listOf("network"), LocationProviderSpec.providers(hasFinePermission = false))
    }

    @Test fun precisePermissionCanUseNetworkAndGpsProviders() {
        assertEquals(listOf("network", "gps"), LocationProviderSpec.providers(hasFinePermission = true))
    }
}
