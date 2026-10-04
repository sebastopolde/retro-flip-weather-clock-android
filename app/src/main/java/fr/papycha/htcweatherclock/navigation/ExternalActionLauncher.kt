package fr.papycha.htcweatherclock.navigation

import android.app.Activity
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.provider.AlarmClock
import fr.papycha.htcweatherclock.MainActivity
import fr.papycha.htcweatherclock.weather.ResolvedLocation
import fr.papycha.htcweatherclock.widget.WidgetActionSpec

object ExternalActionLauncher {
    fun showAlarms(context: Context) {
        if (start(context, Intent(AlarmClock.ACTION_SHOW_ALARMS))) return
        if (start(context, Intent(AlarmClock.ACTION_SET_ALARM))) return
        openDashboard(context)
    }

    fun addAlarm(context: Context) {
        if (start(context, Intent(AlarmClock.ACTION_SET_ALARM))) return
        showAlarms(context)
    }

    fun showWeather(context: Context, location: ResolvedLocation?, fallbackCity: String) {
        if (start(context, Intent(Intent.ACTION_VIEW, Uri.parse(WidgetActionSpec.weatherUrl(location, fallbackCity))))) return
        openDashboard(context)
    }

    private fun openDashboard(context: Context) {
        if (context is MainActivity) return
        start(context, Intent(context, MainActivity::class.java))
    }

    private fun start(context: Context, intent: Intent): Boolean = try {
        if (context !is Activity) intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        context.startActivity(intent)
        true
    } catch (_: ActivityNotFoundException) {
        false
    } catch (_: SecurityException) {
        false
    }
}
