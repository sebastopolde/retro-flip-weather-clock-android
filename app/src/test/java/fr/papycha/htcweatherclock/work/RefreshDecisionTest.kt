package fr.papycha.htcweatherclock.work

import fr.papycha.htcweatherclock.weather.WeatherResult
import org.junit.Assert.assertEquals
import org.junit.Test

class RefreshDecisionTest {
    @Test fun permanentInvalidCityStopsWithoutRetrying() {
        assertEquals(RefreshDecision.COMPLETE, RefreshDecision.from(WeatherResult(null, fresh = false, retryable = false)))
    }

    @Test fun temporaryTransportFailureRequestsRetry() {
        assertEquals(RefreshDecision.RETRY, RefreshDecision.from(WeatherResult(null, fresh = false, retryable = true)))
    }
}
