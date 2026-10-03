package fr.papycha.htcweatherclock.widget

enum class WidgetKind { CLASSIC, WEEKLY }

object WidgetKindSelector {
    fun fromProvider(className: String?): WidgetKind = if (className?.endsWith(".WeeklyWeatherWidgetProvider") == true) WidgetKind.WEEKLY else WidgetKind.CLASSIC
}
