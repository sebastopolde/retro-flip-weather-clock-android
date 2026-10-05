package fr.papycha.htcweatherclock.widget

import org.junit.Assert.assertArrayEquals
import org.junit.Test

class WidgetSyncPlanTest {
    @Test
    fun `dashboard save synchronizes every installed widget once`() {
        assertArrayEquals(
            intArrayOf(4, 9, 12),
            WidgetSyncPlan.targets(0, intArrayOf(4, 9), intArrayOf(9, 12), intArrayOf(12, 4))
        )
    }

    @Test
    fun `configuration save only synchronizes the active widget`() {
        assertArrayEquals(
            intArrayOf(27),
            WidgetSyncPlan.targets(27, intArrayOf(4, 9), intArrayOf(12), intArrayOf(31))
        )
    }
}
