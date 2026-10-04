package fr.papycha.htcweatherclock.widget

import android.appwidget.AppWidgetManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.widget.Toast
import fr.papycha.htcweatherclock.R
import fr.papycha.htcweatherclock.storage.WidgetPreferences
import fr.papycha.htcweatherclock.work.RefreshScheduler

class WidgetPinnedReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val widgetId = intent.getIntExtra(
            AppWidgetManager.EXTRA_APPWIDGET_ID,
            AppWidgetManager.INVALID_APPWIDGET_ID
        )
        if (widgetId == AppWidgetManager.INVALID_APPWIDGET_ID) return

        WidgetPreferences.copyGlobalConfiguration(context, widgetId)
        AppWidgetManager.getInstance(context).updateAppWidget(widgetId, WidgetRenderer.render(context, widgetId))
        Toast.makeText(context, R.string.widget_added, Toast.LENGTH_LONG).show()
        RefreshScheduler.periodic(context, widgetId)
        RefreshScheduler.refresh(context, widgetId)
    }
}
