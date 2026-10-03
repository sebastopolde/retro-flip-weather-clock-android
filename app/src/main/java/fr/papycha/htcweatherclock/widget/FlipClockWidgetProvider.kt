package fr.papycha.htcweatherclock.widget

import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context
import fr.papycha.htcweatherclock.storage.WidgetPreferences
import fr.papycha.htcweatherclock.work.RefreshScheduler

class FlipClockWidgetProvider : AppWidgetProvider() {
    override fun onUpdate(context: Context, manager: AppWidgetManager, ids: IntArray) { ids.forEach { id -> manager.updateAppWidget(id, WidgetRenderer.render(context, id)); RefreshScheduler.periodic(context, id); RefreshScheduler.refresh(context, id) } }
    override fun onAppWidgetOptionsChanged(context: Context, manager: AppWidgetManager, id: Int, options: android.os.Bundle) { manager.updateAppWidget(id, WidgetRenderer.render(context, id)) }
    override fun onDeleted(context: Context, ids: IntArray) = ids.forEach { RefreshScheduler.cancel(context, it); WidgetPreferences.delete(context, it) }
}
