package fr.papycha.htcweatherclock

import org.junit.Assert.assertEquals
import org.junit.Test

class PreviewPageSelectorTest {
    @Test
    fun `swipe left opens weekly preview and cannot go beyond it`() {
        assertEquals(1, PreviewPageSelector.afterSwipe(currentPage = 0, deltaX = -180f))
        assertEquals(1, PreviewPageSelector.afterSwipe(currentPage = 1, deltaX = -180f))
    }

    @Test
    fun `swipe right returns to classic preview and cannot go before it`() {
        assertEquals(0, PreviewPageSelector.afterSwipe(currentPage = 1, deltaX = 180f))
        assertEquals(0, PreviewPageSelector.afterSwipe(currentPage = 0, deltaX = 180f))
    }

    @Test
    fun `short movement does not change preview`() {
        assertEquals(0, PreviewPageSelector.afterSwipe(currentPage = 0, deltaX = -30f))
        assertEquals(1, PreviewPageSelector.afterSwipe(currentPage = 1, deltaX = 30f))
    }

    @Test
    fun `threshold can be scaled for dense displays`() {
        assertEquals(0, PreviewPageSelector.afterSwipe(currentPage = 0, deltaX = -100f, thresholdPx = 216f))
        assertEquals(1, PreviewPageSelector.afterSwipe(currentPage = 0, deltaX = -220f, thresholdPx = 216f))
    }
}
