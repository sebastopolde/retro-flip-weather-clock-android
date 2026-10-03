package fr.papycha.htcweatherclock

import android.app.Activity
import android.appwidget.AppWidgetManager
import android.content.ComponentName
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import fr.papycha.htcweatherclock.storage.WidgetPreferences
import fr.papycha.htcweatherclock.weather.OpenMeteoClient
import fr.papycha.htcweatherclock.widget.FlipClockWidgetProvider

open class MainActivity : Activity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState); setContentView(R.layout.activity_main)
        val input = findViewById<EditText>(R.id.city_input); val status = findViewById<TextView>(R.id.status); input.setText(WidgetPreferences.city(this)); renderPreview()
        findViewById<Button>(R.id.refresh_button).setOnClickListener { button ->
            val city = input.text.toString().trim().ifEmpty { "Paris" }; WidgetPreferences.setCity(this, city); status.text = getString(R.string.loading); button.isEnabled = false
            Thread { val result = OpenMeteoClient.fetch(city, WidgetPreferences.weather(this)); result.snapshot?.let { WidgetPreferences.setWeather(this, it) }; runOnUiThread { button.isEnabled = true; status.text = getString(if (result.fresh) R.string.updated else R.string.offline_cache); renderPreview(); sendBroadcast(Intent(this, FlipClockWidgetProvider::class.java).apply { action = FlipClockWidgetProvider.ACTION_REFRESH }) } }.start()
        }
        findViewById<Button>(R.id.add_widget_button).setOnClickListener { requestPinWidget(status) }
    }
    private fun renderPreview() { val w = WidgetPreferences.weather(this); findViewById<TextView>(R.id.weather_symbol).text = w?.symbol ?: "☁"; findViewById<TextView>(R.id.temperature).text = w?.let { "${it.temperature}°" } ?: "--°"; findViewById<TextView>(R.id.city).text = w?.city ?: getString(R.string.tap_to_configure); findViewById<TextView>(R.id.range).text = w?.let { "${it.minimum}° / ${it.maximum}°" } ?: getString(R.string.weather_offline) }
    private fun requestPinWidget(status: TextView) { val manager = AppWidgetManager.getInstance(this); if (android.os.Build.VERSION.SDK_INT >= 26 && manager.isRequestPinAppWidgetSupported) { manager.requestPinAppWidget(ComponentName(this, FlipClockWidgetProvider::class.java), null, null); status.text = getString(R.string.confirm_on_launcher) } else status.text = getString(R.string.manual_widget_help) }
}
