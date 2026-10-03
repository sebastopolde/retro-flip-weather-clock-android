package fr.papycha.htcweatherclock.widget

enum class WidgetLayout { COMPACT, CLASSIC, WIDE }
object WidgetLayoutSelector { fun select(widthDp: Int, sdk: Int): WidgetLayout = when { widthDp < 180 -> WidgetLayout.COMPACT; widthDp >= 420 -> WidgetLayout.WIDE; else -> WidgetLayout.CLASSIC } }
