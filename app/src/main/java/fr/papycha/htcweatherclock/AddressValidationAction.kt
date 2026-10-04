package fr.papycha.htcweatherclock

enum class AddressValidationTrigger {
    TEST_ADDRESS,
    REFRESH
}

enum class AddressValidationAction {
    APPLY_AND_REFRESH;

    companion object {
        fun forTrigger(trigger: AddressValidationTrigger): AddressValidationAction = when (trigger) {
            AddressValidationTrigger.TEST_ADDRESS,
            AddressValidationTrigger.REFRESH -> APPLY_AND_REFRESH
        }
    }
}
