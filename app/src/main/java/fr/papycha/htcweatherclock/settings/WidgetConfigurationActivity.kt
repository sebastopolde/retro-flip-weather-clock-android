package fr.papycha.htcweatherclock.settings

import android.appwidget.AppWidgetManager
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import fr.papycha.htcweatherclock.MainActivity
import fr.papycha.htcweatherclock.R
import fr.papycha.htcweatherclock.storage.WidgetPreferences
import fr.papycha.htcweatherclock.widget.WidgetRenderer
import fr.papycha.htcweatherclock.work.RefreshScheduler

class WidgetConfigurationActivity : MainActivity() {
    override fun onCreate(savedInstanceState: Bundle?) { setResult(RESULT_CANCELED); super.onCreate(savedInstanceState); val id = intent.getIntExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, AppWidgetManager.INVALID_APPWIDGET_ID); if (id >= 0) { findViewById<Button>(R.id.add_widget_button).apply { text = getString(R.string.save_widget); setOnClickListener { WidgetPreferences.setCity(this@WidgetConfigurationActivity, findViewById<EditText>(R.id.city_input).text.toString(), id); AppWidgetManager.getInstance(this@WidgetConfigurationActivity).updateAppWidget(id, WidgetRenderer.render(this@WidgetConfigurationActivity, id)); RefreshScheduler.periodic(this@WidgetConfigurationActivity, id); RefreshScheduler.refresh(this@WidgetConfigurationActivity, id); setResult(RESULT_OK, Intent().putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, id)); finish() } } } }
}
