package fr.papycha.htcweatherclock

import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class RequestGateTest {
    @Test fun newerRequestInvalidatesOlderCompletion() {
        val gate = RequestGate()
        val first = gate.start()
        val second = gate.start()

        assertFalse(gate.isCurrent(first))
        assertTrue(gate.isCurrent(second))
    }

    @Test fun closingScreenInvalidatesPendingCompletion() {
        val gate = RequestGate()
        val request = gate.start()

        gate.close()

        assertFalse(gate.isCurrent(request))
    }
}
