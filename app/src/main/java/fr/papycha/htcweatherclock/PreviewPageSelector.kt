package fr.papycha.htcweatherclock

object PreviewPageSelector {
    private const val DEFAULT_SWIPE_THRESHOLD_PX = 72f
    const val PAGE_COUNT = 3

    fun afterSwipe(
        currentPage: Int,
        deltaX: Float,
        thresholdPx: Float = DEFAULT_SWIPE_THRESHOLD_PX
    ): Int = when {
        deltaX <= -thresholdPx -> currentPage + 1
        deltaX >= thresholdPx -> currentPage - 1
        else -> currentPage
    }.coerceIn(0, PAGE_COUNT - 1)
}
