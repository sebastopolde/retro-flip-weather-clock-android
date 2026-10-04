package fr.papycha.htcweatherclock.widget

object WidgetSyncPlan {
    fun targets(activeWidgetId: Int, classicIds: IntArray, weeklyIds: IntArray): IntArray {
        if (activeWidgetId != 0) return intArrayOf(activeWidgetId)
        return linkedSetOf<Int>().apply {
            addAll(classicIds.toList())
            addAll(weeklyIds.toList())
        }.toIntArray()
    }
}
