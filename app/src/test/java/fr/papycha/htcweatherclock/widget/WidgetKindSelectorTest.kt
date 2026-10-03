package fr.papycha.htcweatherclock.widget

import org.junit.Assert.assertEquals
import org.junit.Test

class WidgetKindSelectorTest {
    @Test fun weeklyProviderSelectsWeeklyRenderer() {
        assertEquals(WidgetKind.WEEKLY, WidgetKindSelector.fromProvider("fr.papycha.htcweatherclock.widget.WeeklyWeatherWidgetProvider"))
    }

    @Test fun classicAndMissingProvidersKeepClassicRenderer() {
        assertEquals(WidgetKind.CLASSIC, WidgetKindSelector.fromProvider("fr.papycha.htcweatherclock.widget.FlipClockWidgetProvider"))
        assertEquals(WidgetKind.CLASSIC, WidgetKindSelector.fromProvider(null))
    }
}
