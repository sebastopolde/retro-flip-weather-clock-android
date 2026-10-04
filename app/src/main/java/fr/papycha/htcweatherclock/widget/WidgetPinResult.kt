package fr.papycha.htcweatherclock.widget

internal object WidgetPinResult {
    fun canUseSavedConfiguration(
        input: String,
        savedLocationLabel: String?,
        savedCity: String,
        hasWeather: Boolean
    ): Boolean {
        val savedLabel = savedLocationLabel ?: savedCity
        return hasWeather && input.trim().equals(savedLabel.trim(), ignoreCase = true)
    }
}
