package fr.papycha.htcweatherclock.widget

import fr.papycha.htcweatherclock.weather.ResolvedLocation
import java.net.URLEncoder
import java.util.Locale

enum class WidgetAction(val requestOffset: Int) {
    ALARMS(1),
    WEATHER_DETAILS(2)
}

object WidgetActionSpec {
    fun weatherCity(configuredCity: String, cachedCity: String?): String =
        configuredCity.trim().ifEmpty { cachedCity.orEmpty().trim().ifEmpty { "Paris" } }

    fun weatherUrl(location: ResolvedLocation?, fallbackCity: String): String {
        if (location != null) {
            val latitude = String.format(Locale.US, "%.5f", location.latitude)
            val longitude = String.format(Locale.US, "%.5f", location.longitude)
            return "https://www.windy.com/$latitude/$longitude?$latitude,$longitude,11"
        }
        val safeCity = fallbackCity.trim().ifEmpty { "Paris" }
        return "https://www.windy.com/search?query=${URLEncoder.encode(safeCity, "UTF-8")}"
    }

    fun requestCode(appWidgetId: Int, action: WidgetAction): Int = appWidgetId * 10 + action.requestOffset
}
