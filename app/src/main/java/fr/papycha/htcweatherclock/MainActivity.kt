package fr.papycha.htcweatherclock

import android.app.Activity
import android.appwidget.AppWidgetManager
import android.content.ComponentName
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import fr.papycha.htcweatherclock.storage.WidgetPreferences
import fr.papycha.htcweatherclock.weather.OpenMeteoClient
import fr.papycha.htcweatherclock.widget.FlipClockWidgetProvider
import fr.papycha.htcweatherclock.widget.WidgetRenderer
import fr.papycha.htcweatherclock.work.RefreshScheduler

open class MainActivity : Activity() {
    protected var activeWidgetId: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState); setContentView(R.layout.activity_main)
        activeWidgetId = intent.getIntExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, 0).takeIf { it != AppWidgetManager.INVALID_APPWIDGET_ID } ?: 0
        val input = findViewById<EditText>(R.id.city_input); val status = findViewById<TextView>(R.id.status); input.setText(WidgetPreferences.city(this, activeWidgetId)); renderPreview()
        findViewById<Button>(R.id.refresh_button).setOnClickListener { button ->
            val city = input.text.toString().trim().ifEmpty { "Paris" }; WidgetPreferences.setCity(this, city, activeWidgetId); status.text = getString(R.string.loading); button.isEnabled = false
            Thread { val result = OpenMeteoClient.fetch(city, WidgetPreferences.weather(this, activeWidgetId)); result.snapshot?.let { WidgetPreferences.setWeather(this, it, activeWidgetId) }; runOnUiThread { button.isEnabled = true; status.text = getString(if (result.fresh) R.string.updated else R.string.offline_cache); renderPreview(); if (activeWidgetId != 0) AppWidgetManager.getInstance(this).updateAppWidget(activeWidgetId, WidgetRenderer.render(this, activeWidgetId)) } }.start()
        }
        findViewById<Button>(R.id.add_widget_button).apply {
            if (activeWidgetId != 0) text = getString(R.string.save_widget)
            setOnClickListener {
                if (activeWidgetId == 0) requestPinWidget(status) else {
                    WidgetPreferences.setCity(this@MainActivity, input.text.toString(), activeWidgetId)
                    AppWidgetManager.getInstance(this@MainActivity).updateAppWidget(activeWidgetId, WidgetRenderer.render(this@MainActivity, activeWidgetId))
                    RefreshScheduler.refresh(this@MainActivity, activeWidgetId); status.text = getString(R.string.updated)
                }
            }
        }
    }
    protected fun renderPreview() { val w = WidgetPreferences.weather(this, activeWidgetId); findViewById<TextView>(R.id.weather_symbol).text = w?.symbol ?: "☁"; findViewById<TextView>(R.id.temperature).text = w?.let { "${it.temperature}°" } ?: "--°"; findViewById<TextView>(R.id.city).text = w?.city ?: WidgetPreferences.city(this, activeWidgetId); findViewById<TextView>(R.id.range).text = w?.let { "${it.minimum}° / ${it.maximum}°" } ?: getString(R.string.weather_offline) }
    private fun requestPinWidget(status: TextView) { val manager = AppWidgetManager.getInstance(this); if (android.os.Build.VERSION.SDK_INT >= 26 && manager.isRequestPinAppWidgetSupported) { manager.requestPinAppWidget(ComponentName(this, FlipClockWidgetProvider::class.java), null, null); status.text = getString(R.string.confirm_on_launcher) } else status.text = getString(R.string.manual_widget_help) }
}
