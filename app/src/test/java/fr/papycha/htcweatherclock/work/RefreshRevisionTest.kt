package fr.papycha.htcweatherclock.work

import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class RefreshRevisionTest {
    @Test
    fun `worker result is accepted while widget configuration is unchanged`() {
        assertTrue(RefreshRevision.canApply(startRevision = 7L, currentRevision = 7L))
    }

    @Test
    fun `worker result is discarded after widget configuration changes`() {
        assertFalse(RefreshRevision.canApply(startRevision = 7L, currentRevision = 8L))
    }
}
