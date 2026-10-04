package fr.papycha.htcweatherclock

class RequestGate {
    @Volatile private var generation = 0
    @Volatile private var closed = false

    @Synchronized fun start(): Int {
        closed = false
        return ++generation
    }

    fun isCurrent(request: Int): Boolean = !closed && request == generation

    @Synchronized fun close() {
        closed = true
        generation++
    }
}
