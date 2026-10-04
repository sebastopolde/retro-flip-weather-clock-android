package fr.papycha.htcweatherclock.weather

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test
import java.io.IOException

class GeocodingSpecTest {
    @Test fun completePostalAddressIsEncodedWithoutLosingItsParts() {
        assertEquals(
            "https://nominatim.openstreetmap.org/search?format=jsonv2&limit=1&addressdetails=1&accept-language=fr&q=12+rue+de+la+Paix%2C+75002+Paris",
            GeocodingSpec.searchUrl("12 rue de la Paix, 75002 Paris")
        )
    }

    @Test fun validSearchResultReturnsRecognizedLabelAndCoordinates() {
        val json = """[{"lat":"48.86869","lon":"2.33059","display_name":"Rue de la Paix, Quartier de la Place-Vendôme, Paris, France"}]"""

        assertEquals(
            ResolvedLocation("Rue de la Paix, Quartier de la Place-Vendôme, Paris, France", 48.86869, 2.33059),
            GeocodingSpec.parseFirst(json)
        )
    }

    @Test fun emptySearchResultIsNotAcceptedAsAnAddress() {
        assertNull(GeocodingSpec.parseFirst("[]"))
    }

    @Test fun postalAddressKeepsFullLabelButUsesTownOnTheWidget() {
        val json = """[{"lat":"48.86869","lon":"2.33059","display_name":"Place de la Concorde, Paris, 75008, France","address":{"city":"Paris","postcode":"75008","country":"France"}}]"""

        val location = GeocodingSpec.parseFirst(json)!!

        assertEquals("Place de la Concorde, Paris, 75008, France", location.label)
        assertEquals("Paris", location.weatherLabel)
    }

    @Test fun coordinatesProduceDirectForecastUrl() {
        assertEquals(
            "https://api.open-meteo.com/v1/forecast?latitude=48.86869&longitude=2.33059&current=temperature_2m,weather_code&daily=weather_code,temperature_2m_max,temperature_2m_min&timezone=auto&forecast_days=7",
            GeocodingSpec.forecastUrl(48.86869, 2.33059)
        )
    }

    @Test fun emptyResultIsReportedAsNotFound() {
        assertEquals(AddressResolution.NotFound, AddressResolver.resolve("adresse") { "[]" })
    }

    @Test fun connectionFailureIsNotReportedAsAnInvalidAddress() {
        assertEquals(AddressResolution.NetworkError, AddressResolver.resolve("adresse") { throw IOException("offline") })
    }

    @Test fun malformedServiceResponseIsReportedSeparately() {
        assertEquals(AddressResolution.ServiceError, AddressResolver.resolve("adresse") { "not-json" })
    }

    @Test fun httpRateLimitIsReportedAsServiceFailureNotConnectivityFailure() {
        assertEquals(
            AddressResolution.ServiceError,
            AddressResolver.resolve("adresse") { throw RemoteServiceException(429) }
        )
    }
}
