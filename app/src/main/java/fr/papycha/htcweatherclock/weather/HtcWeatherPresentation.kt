package fr.papycha.htcweatherclock.weather

data class HtcWeatherVisual(val iconCode: Int, val label: String)

object HtcWeatherPresentation {
    fun forWmo(code: Int): HtcWeatherVisual = when (code) {
        0 -> HtcWeatherVisual(1, "Ensoleillé")
        1 -> HtcWeatherVisual(2, "Plutôt ensoleillé")
        2 -> HtcWeatherVisual(3, "Partiellement nuageux")
        3 -> HtcWeatherVisual(7, "Nuageux")
        in 45..48 -> HtcWeatherVisual(11, "Brouillard")
        in 51..55 -> HtcWeatherVisual(12, "Bruine")
        in 56..57 -> HtcWeatherVisual(25, "Bruine verglaçante")
        in 61..67, in 80..82 -> HtcWeatherVisual(18, "Pluie")
        in 71..77, in 85..86 -> HtcWeatherVisual(22, "Neige")
        in 95..99 -> HtcWeatherVisual(15, "Orages")
        else -> HtcWeatherVisual(7, "Conditions variables")
    }
}
