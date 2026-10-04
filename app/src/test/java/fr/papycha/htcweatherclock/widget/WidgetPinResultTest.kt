package fr.papycha.htcweatherclock.widget

import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class WidgetPinResultTest {
    @Test
    fun `saved weather can be reused only for the address currently displayed`() {
        assertTrue(WidgetPinResult.canUseSavedConfiguration(" Paris ", "paris", "Paris", hasWeather = true))
        assertTrue(WidgetPinResult.canUseSavedConfiguration("Paris", null, "Paris", hasWeather = true))
        assertFalse(WidgetPinResult.canUseSavedConfiguration("Lyon", "Paris", "Paris", hasWeather = true))
        assertFalse(WidgetPinResult.canUseSavedConfiguration("Paris", "Paris", "Paris", hasWeather = false))
    }
}
