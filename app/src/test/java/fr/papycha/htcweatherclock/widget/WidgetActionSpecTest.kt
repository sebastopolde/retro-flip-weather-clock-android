package fr.papycha.htcweatherclock.widget

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotEquals
import org.junit.Test

class WidgetActionSpecTest {
    @Test fun weatherPageEncodesTheConfiguredCity() {
        assertEquals(
            "https://www.google.com/search?q=m%C3%A9t%C3%A9o+Saint-Maurice-Montcouronne",
            WidgetActionSpec.weatherUrl("Saint-Maurice-Montcouronne")
        )
    }

    @Test fun weatherPageFallsBackToParisForABlankCity() {
        assertEquals(
            "https://www.google.com/search?q=m%C3%A9t%C3%A9o+Paris",
            WidgetActionSpec.weatherUrl("   ")
        )
    }

    @Test fun configuredCityWinsOverAnOlderCachedWeatherCity() {
        assertEquals(
            "Lyon",
            WidgetActionSpec.weatherCity(configuredCity = " Lyon ", cachedCity = "Paris")
        )
    }

    @Test fun cachedCityIsUsedOnlyWhenConfigurationIsBlank() {
        assertEquals(
            "Bordeaux",
            WidgetActionSpec.weatherCity(configuredCity = " ", cachedCity = " Bordeaux ")
        )
    }

    @Test fun clockAndWeatherPendingIntentsCannotReplaceEachOther() {
        assertNotEquals(
            WidgetActionSpec.requestCode(42, WidgetAction.ALARMS),
            WidgetActionSpec.requestCode(42, WidgetAction.WEATHER_DETAILS)
        )
        assertNotEquals(
            WidgetActionSpec.requestCode(42, WidgetAction.WEATHER_DETAILS),
            WidgetActionSpec.requestCode(43, WidgetAction.WEATHER_DETAILS)
        )
    }
}
