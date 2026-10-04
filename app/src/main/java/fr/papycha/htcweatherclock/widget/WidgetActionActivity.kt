package fr.papycha.htcweatherclock.widget

import android.app.Activity
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Bundle
import fr.papycha.htcweatherclock.navigation.ExternalActionLauncher
import fr.papycha.htcweatherclock.weather.ResolvedLocation

class WidgetActionActivity : Activity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        when (intent.getStringExtra(EXTRA_ACTION)?.let { runCatching { WidgetAction.valueOf(it) }.getOrNull() }) {
            WidgetAction.ALARMS -> ExternalActionLauncher.showAlarms(this)
            WidgetAction.WEATHER_DETAILS -> ExternalActionLauncher.showWeather(
                this,
                intent.resolvedLocation(),
                intent.getStringExtra(EXTRA_CITY).orEmpty()
            )
            null -> Unit
        }
        finish()
    }

    companion object {
        private const val EXTRA_ACTION = "widget_action"
        private const val EXTRA_CITY = "widget_city"
        private const val EXTRA_LOCATION_LABEL = "widget_location_label"
        private const val EXTRA_LATITUDE = "widget_latitude"
        private const val EXTRA_LONGITUDE = "widget_longitude"

        fun pendingIntent(
            context: Context,
            appWidgetId: Int,
            action: WidgetAction,
            city: String = "",
            location: ResolvedLocation? = null
        ): PendingIntent {
            val intent = Intent(context, WidgetActionActivity::class.java)
                .putExtra(EXTRA_ACTION, action.name)
                .putExtra(EXTRA_CITY, city)
            if (location != null) {
                intent.putExtra(EXTRA_LOCATION_LABEL, location.label)
                    .putExtra(EXTRA_LATITUDE, location.latitude)
                    .putExtra(EXTRA_LONGITUDE, location.longitude)
            }
            return PendingIntent.getActivity(
                context,
                WidgetActionSpec.requestCode(appWidgetId, action),
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )
        }

        private fun Intent.resolvedLocation(): ResolvedLocation? {
            if (!hasExtra(EXTRA_LATITUDE) || !hasExtra(EXTRA_LONGITUDE)) return null
            val label = getStringExtra(EXTRA_LOCATION_LABEL).orEmpty().ifBlank {
                getStringExtra(EXTRA_CITY).orEmpty().ifBlank { "Position météo" }
            }
            return ResolvedLocation(label, getDoubleExtra(EXTRA_LATITUDE, 0.0), getDoubleExtra(EXTRA_LONGITUDE, 0.0))
        }
    }
}
