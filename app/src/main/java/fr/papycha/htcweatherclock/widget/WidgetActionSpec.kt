package fr.papycha.htcweatherclock.widget

import java.net.URLEncoder

enum class WidgetAction(val requestOffset: Int) {
    ALARMS(1),
    WEATHER_DETAILS(2)
}

object WidgetActionSpec {
    fun weatherCity(configuredCity: String, cachedCity: String?): String =
        configuredCity.trim().ifEmpty { cachedCity.orEmpty().trim().ifEmpty { "Paris" } }

    fun weatherUrl(city: String): String {
        val safeCity = city.trim().ifEmpty { "Paris" }
        val query = URLEncoder.encode("météo $safeCity", "UTF-8")
        return "https://www.google.com/search?q=$query"
    }

    fun requestCode(appWidgetId: Int, action: WidgetAction): Int = appWidgetId * 10 + action.requestOffset
}
