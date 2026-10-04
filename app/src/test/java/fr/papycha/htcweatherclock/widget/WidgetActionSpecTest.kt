package fr.papycha.htcweatherclock.widget

import fr.papycha.htcweatherclock.weather.ResolvedLocation
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotEquals
import org.junit.Test

class WidgetActionSpecTest {
    @Test fun weatherPageOpensWindyAtTheExactResolvedCoordinates() {
        assertEquals(
            "https://www.windy.com/48.85660/2.35220?48.85660,2.35220,11",
            WidgetActionSpec.weatherUrl(
                ResolvedLocation("Paris", 48.8566, 2.3522),
                fallbackCity = "Paris"
            )
        )
    }

    @Test fun weatherPageKeepsNegativeCoordinatesAndDecimalPoints() {
        assertEquals(
            "https://www.windy.com/-33.86880/151.20930?-33.86880,151.20930,11",
            WidgetActionSpec.weatherUrl(
                ResolvedLocation("Sydney", -33.8688, 151.2093),
                fallbackCity = "Sydney"
            )
        )
    }

    @Test fun legacyWeatherPageFallsBackToAWindyLocationSearch() {
        assertEquals(
            "https://www.windy.com/search?query=Saint-Maurice-Montcouronne",
            WidgetActionSpec.weatherUrl(location = null, fallbackCity = "Saint-Maurice-Montcouronne")
        )
    }

    @Test fun blankLegacyWeatherPageFallsBackToParis() {
        assertEquals(
            "https://www.windy.com/search?query=Paris",
            WidgetActionSpec.weatherUrl(location = null, fallbackCity = "   ")
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
