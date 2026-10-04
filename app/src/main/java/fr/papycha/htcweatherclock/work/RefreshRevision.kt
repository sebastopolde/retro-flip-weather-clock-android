package fr.papycha.htcweatherclock.work

object RefreshRevision {
    fun canApply(startRevision: Long, currentRevision: Long): Boolean =
        startRevision == currentRevision
}
