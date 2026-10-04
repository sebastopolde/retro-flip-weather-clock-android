package fr.papycha.htcweatherclock

import org.junit.Assert.assertEquals
import org.junit.Test

class AddressValidationActionTest {
    @Test fun successfulTestAddressAppliesAndRefreshesWidgets() {
        assertEquals(
            AddressValidationAction.APPLY_AND_REFRESH,
            AddressValidationAction.forTrigger(AddressValidationTrigger.TEST_ADDRESS)
        )
    }

    @Test fun explicitRefreshStillAppliesAndRefreshesWidgets() {
        assertEquals(
            AddressValidationAction.APPLY_AND_REFRESH,
            AddressValidationAction.forTrigger(AddressValidationTrigger.REFRESH)
        )
    }
}
