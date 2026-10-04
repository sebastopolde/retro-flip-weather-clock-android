package fr.papycha.htcweatherclock.storage

object WidgetStateTransfer {
    fun fromGlobal(values: Map<String, *>, targetWidgetId: Int): Map<String, Any> {
        val result = linkedMapOf<String, Any>()
        values.forEach { (key, value) ->
            val targetKey = when {
                key == "city_0" -> "city_$targetWidgetId"
                key.startsWith("location_0_") -> key.replaceFirst("location_0_", "location_${targetWidgetId}_")
                key.startsWith("weather_0_") -> key.replaceFirst("weather_0_", "weather_${targetWidgetId}_")
                else -> null
            }
            if (targetKey != null && value != null) result[targetKey] = value
        }
        return result
    }
}
