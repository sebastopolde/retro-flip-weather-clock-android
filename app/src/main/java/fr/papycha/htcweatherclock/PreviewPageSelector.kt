package fr.papycha.htcweatherclock

object PreviewPageSelector {
    private const val DEFAULT_SWIPE_THRESHOLD_PX = 72f

    fun afterSwipe(
        currentPage: Int,
        deltaX: Float,
        thresholdPx: Float = DEFAULT_SWIPE_THRESHOLD_PX
    ): Int = when {
        deltaX <= -thresholdPx -> 1
        deltaX >= thresholdPx -> 0
        else -> currentPage.coerceIn(0, 1)
    }
}
