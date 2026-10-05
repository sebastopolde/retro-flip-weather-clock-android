package fr.papycha.htcweatherclock.widget

enum class WidgetKind { CLASSIC, WEEKLY, COMPACT_WEEKLY }

object WidgetKindSelector {
    fun fromProvider(className: String?): WidgetKind = when {
        className?.endsWith(".CompactWeeklyWidgetProvider") == true -> WidgetKind.COMPACT_WEEKLY
        className?.endsWith(".WeeklyWeatherWidgetProvider") == true -> WidgetKind.WEEKLY
        else -> WidgetKind.CLASSIC
    }
}
