package fr.papycha.htcweatherclock.work

import fr.papycha.htcweatherclock.weather.WeatherResult

enum class RefreshDecision {
    COMPLETE, RETRY;

    companion object {
        fun from(result: WeatherResult): RefreshDecision = if (!result.fresh && result.retryable) RETRY else COMPLETE
    }
}
