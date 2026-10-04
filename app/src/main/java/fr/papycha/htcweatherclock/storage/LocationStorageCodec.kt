package fr.papycha.htcweatherclock.storage

import fr.papycha.htcweatherclock.weather.ResolvedLocation

object LocationStorageCodec {
    fun decode(values: Map<String, *>, widgetId: Int): ResolvedLocation? {
        val prefix = "location_$widgetId"
        val label = values["${prefix}_label"] as? String ?: return null
        val latitude = (values["${prefix}_latitude"] as? String)?.toDoubleOrNull() ?: return null
        val longitude = (values["${prefix}_longitude"] as? String)?.toDoubleOrNull() ?: return null
        val weatherLabel = values["${prefix}_weather_label"] as? String ?: label
        return ResolvedLocation(label, latitude, longitude, weatherLabel)
    }
}
