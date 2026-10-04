package fr.papycha.htcweatherclock.widget

import android.app.Activity
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Bundle
import fr.papycha.htcweatherclock.navigation.ExternalActionLauncher

class WidgetActionActivity : Activity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        when (intent.getStringExtra(EXTRA_ACTION)?.let { runCatching { WidgetAction.valueOf(it) }.getOrNull() }) {
            WidgetAction.ALARMS -> ExternalActionLauncher.showAlarms(this)
            WidgetAction.WEATHER_DETAILS -> ExternalActionLauncher.showWeather(this, intent.getStringExtra(EXTRA_CITY).orEmpty())
            null -> Unit
        }
        finish()
    }

    companion object {
        private const val EXTRA_ACTION = "widget_action"
        private const val EXTRA_CITY = "widget_city"

        fun pendingIntent(context: Context, appWidgetId: Int, action: WidgetAction, city: String = ""): PendingIntent {
            val intent = Intent(context, WidgetActionActivity::class.java)
                .putExtra(EXTRA_ACTION, action.name)
                .putExtra(EXTRA_CITY, city)
            return PendingIntent.getActivity(
                context,
                WidgetActionSpec.requestCode(appWidgetId, action),
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )
        }
    }
}
