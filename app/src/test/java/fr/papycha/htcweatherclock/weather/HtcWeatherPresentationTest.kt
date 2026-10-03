package fr.papycha.htcweatherclock.weather

import org.junit.Assert.assertEquals
import org.junit.Test

class HtcWeatherPresentationTest {
    @Test fun clearAndCloudyConditionsUseClassicHtcCodes() {
        assertEquals(HtcWeatherVisual(1, "Ensoleillé"), HtcWeatherPresentation.forWmo(0))
        assertEquals(HtcWeatherVisual(3, "Partiellement nuageux"), HtcWeatherPresentation.forWmo(2))
        assertEquals(HtcWeatherVisual(7, "Nuageux"), HtcWeatherPresentation.forWmo(3))
    }

    @Test fun precipitationConditionsUseMatchingHtcArtwork() {
        assertEquals(HtcWeatherVisual(11, "Brouillard"), HtcWeatherPresentation.forWmo(45))
        assertEquals(HtcWeatherVisual(18, "Pluie"), HtcWeatherPresentation.forWmo(63))
        assertEquals(HtcWeatherVisual(22, "Neige"), HtcWeatherPresentation.forWmo(75))
        assertEquals(HtcWeatherVisual(15, "Orages"), HtcWeatherPresentation.forWmo(96))
    }

    @Test fun unknownConditionsFallBackToHtcCloudArtwork() {
        assertEquals(HtcWeatherVisual(7, "Conditions variables"), HtcWeatherPresentation.forWmo(999))
    }
}
