package fr.papycha.htcweatherclock

import android.app.Activity
import android.appwidget.AppWidgetManager
import android.content.ComponentName
import android.os.Bundle
import android.os.Build
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.view.View
import android.view.WindowInsets
import fr.papycha.htcweatherclock.navigation.ExternalActionLauncher
import fr.papycha.htcweatherclock.storage.WidgetPreferences
import fr.papycha.htcweatherclock.weather.OpenMeteoClient
import fr.papycha.htcweatherclock.weather.HtcWeatherPresentation
import fr.papycha.htcweatherclock.weather.HtcWeatherDrawable
import fr.papycha.htcweatherclock.widget.FlipClockWidgetProvider
import fr.papycha.htcweatherclock.widget.WeeklyWeatherWidgetProvider
import fr.papycha.htcweatherclock.widget.WidgetRenderer
import fr.papycha.htcweatherclock.work.RefreshScheduler
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

open class MainActivity : Activity() {
    protected var activeWidgetId: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState); setContentView(R.layout.activity_main); applySystemBarInsets()
        activeWidgetId = intent.getIntExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, 0).takeIf { it != AppWidgetManager.INVALID_APPWIDGET_ID } ?: 0
        val input = findViewById<EditText>(R.id.city_input); val status = findViewById<TextView>(R.id.status); input.setText(WidgetPreferences.city(this, activeWidgetId)); renderPreview()
        findViewById<TextView>(R.id.app_version).text = getString(R.string.version_format, BuildConfig.VERSION_NAME)
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
        findViewById<Button>(R.id.add_weekly_widget_button).apply {
            visibility = if (activeWidgetId == 0) View.VISIBLE else View.GONE
            setOnClickListener { requestPinWidget(status, WeeklyWeatherWidgetProvider::class.java) }
        }
        findViewById<Button>(R.id.weather_details_button).setOnClickListener {
            ExternalActionLauncher.showWeather(this, input.text.toString())
        }
        findViewById<Button>(R.id.alarms_button).setOnClickListener {
            ExternalActionLauncher.showAlarms(this)
        }
        findViewById<Button>(R.id.add_alarm_button).setOnClickListener {
            ExternalActionLauncher.addAlarm(this)
        }
    }
    protected fun renderPreview() {
        val weather = WidgetPreferences.weather(this, activeWidgetId)
        val visual = weather?.let { HtcWeatherPresentation.forWmo(it.weatherCode) }
        findViewById<ImageView>(R.id.weather_icon).setImageResource(HtcWeatherDrawable.forCode(visual?.iconCode ?: 7))
        findViewById<TextView>(R.id.temperature).text = weather?.let { "${it.temperature}°" } ?: "--°"
        findViewById<TextView>(R.id.city).text = weather?.city ?: WidgetPreferences.city(this, activeWidgetId)
        findViewById<TextView>(R.id.condition).text = visual?.label ?: getString(R.string.weather_offline)
        findViewById<TextView>(R.id.range).text = weather?.let { "${it.maximum}° / ${it.minimum}°" } ?: "--° / --°"
        findViewById<TextView>(R.id.last_updated).text = weather?.let {
            val formatted = SimpleDateFormat("d MMM yyyy 'à' HH:mm", Locale.FRENCH).format(Date(it.updatedAt))
            getString(R.string.last_updated_format, formatted)
        } ?: getString(R.string.no_weather_update)
    }
    private fun requestPinWidget(status: TextView, provider: Class<*> = FlipClockWidgetProvider::class.java) { val manager = AppWidgetManager.getInstance(this); if (android.os.Build.VERSION.SDK_INT >= 26 && manager.isRequestPinAppWidgetSupported) { manager.requestPinAppWidget(ComponentName(this, provider), null, null); status.text = getString(R.string.confirm_on_launcher) } else status.text = getString(R.string.manual_widget_help) }

    @Suppress("DEPRECATION")
    private fun applySystemBarInsets() {
        val root = findViewById<View>(R.id.dashboard_root)
        root.setOnApplyWindowInsetsListener { view, insets ->
            val top: Int
            val bottom: Int
            if (Build.VERSION.SDK_INT >= 30) {
                val bars = insets.getInsets(WindowInsets.Type.systemBars())
                top = bars.top
                bottom = bars.bottom
            } else {
                top = insets.systemWindowInsetTop
                bottom = insets.systemWindowInsetBottom
            }
            view.setPadding(view.paddingLeft, top, view.paddingRight, bottom)
            insets
        }
        root.requestApplyInsets()
    }
}
