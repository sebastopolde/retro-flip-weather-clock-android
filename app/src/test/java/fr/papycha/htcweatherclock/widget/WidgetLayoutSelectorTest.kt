package fr.papycha.htcweatherclock.widget

import org.junit.Assert.assertEquals
import org.junit.Test

class WidgetLayoutSelectorTest {
    @Test fun narrowLaunchersNeverProduceAnInvalidLayout() {
        assertEquals(WidgetLayout.COMPACT, WidgetLayoutSelector.select(109, 36))
        assertEquals(WidgetLayout.COMPACT, WidgetLayoutSelector.select(179, 23))
    }

    @Test fun regularFourColumnWidgetUsesClassicLayout() {
        assertEquals(WidgetLayout.CLASSIC, WidgetLayoutSelector.select(180, 23))
        assertEquals(WidgetLayout.CLASSIC, WidgetLayoutSelector.select(419, 36))
    }

    @Test fun wideWidgetShowsExtendedWeather() {
        assertEquals(WidgetLayout.WIDE, WidgetLayoutSelector.select(420, 23))
        assertEquals(WidgetLayout.WIDE, WidgetLayoutSelector.select(800, 36))
    }
}
