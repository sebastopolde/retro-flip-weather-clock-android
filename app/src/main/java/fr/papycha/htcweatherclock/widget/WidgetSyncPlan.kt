package fr.papycha.htcweatherclock.widget

object WidgetSyncPlan {
    fun targets(activeWidgetId: Int, vararg providerIds: IntArray): IntArray {
        if (activeWidgetId != 0) return intArrayOf(activeWidgetId)
        return linkedSetOf<Int>().apply {
            providerIds.forEach { addAll(it.toList()) }
        }.toIntArray()
    }
}
