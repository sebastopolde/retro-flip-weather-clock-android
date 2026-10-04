package fr.papycha.htcweatherclock

import android.Manifest
import android.app.Activity
import android.appwidget.AppWidgetManager
import android.content.ComponentName
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.WindowInsets
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import fr.papycha.htcweatherclock.location.AndroidLocationLocator
import fr.papycha.htcweatherclock.navigation.ExternalActionLauncher
import fr.papycha.htcweatherclock.storage.WidgetPreferences
import fr.papycha.htcweatherclock.weather.HtcWeatherDrawable
import fr.papycha.htcweatherclock.weather.HtcWeatherPresentation
import fr.papycha.htcweatherclock.weather.OpenMeteoClient
import fr.papycha.htcweatherclock.weather.AddressResolution
import fr.papycha.htcweatherclock.weather.ResolvedLocation
import fr.papycha.htcweatherclock.weather.WeatherSnapshot
import fr.papycha.htcweatherclock.widget.FlipClockWidgetProvider
import fr.papycha.htcweatherclock.widget.WeeklyWeatherWidgetProvider
import fr.papycha.htcweatherclock.widget.WidgetRenderer
import fr.papycha.htcweatherclock.work.RefreshScheduler
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

open class MainActivity : Activity() {
    protected var activeWidgetId: Int = 0
    private lateinit var input: EditText
    private lateinit var status: TextView
    private lateinit var testAddressButton: Button
    private lateinit var locationButton: Button
    private lateinit var refreshButton: Button
    private lateinit var locator: AndroidLocationLocator
    private val requestGate = RequestGate()
    private var pendingLocation: ResolvedLocation? = null
    private var pendingInput: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        applySystemBarInsets()
        activeWidgetId = intent.getIntExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, 0)
            .takeIf { it != AppWidgetManager.INVALID_APPWIDGET_ID } ?: 0
        input = findViewById(R.id.city_input)
        status = findViewById(R.id.status)
        testAddressButton = findViewById(R.id.test_address_button)
        locationButton = findViewById(R.id.location_button)
        refreshButton = findViewById(R.id.refresh_button)
        locator = AndroidLocationLocator(this)
        pendingLocation = WidgetPreferences.location(this, activeWidgetId)
        pendingInput = pendingLocation?.label
        input.setText(WidgetPreferences.city(this, activeWidgetId))
        renderPreview()
        findViewById<TextView>(R.id.app_version).text = getString(R.string.version_format, BuildConfig.VERSION_NAME)

        testAddressButton.setOnClickListener { validateAddress(save = false) }
        locationButton.setOnClickListener { requestLocationFromUser() }
        refreshButton.setOnClickListener { saveCurrentLocation() }

        findViewById<Button>(R.id.add_widget_button).apply {
            if (activeWidgetId != 0) text = getString(R.string.save_widget)
            setOnClickListener {
                if (activeWidgetId == 0) requestPinWidget(status) else saveCurrentLocation { saved ->
                    if (saved) {
                        AppWidgetManager.getInstance(this@MainActivity)
                            .updateAppWidget(activeWidgetId, WidgetRenderer.render(this@MainActivity, activeWidgetId))
                        RefreshScheduler.refresh(this@MainActivity, activeWidgetId)
                    }
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
        findViewById<Button>(R.id.alarms_button).setOnClickListener { ExternalActionLauncher.showAlarms(this) }
        findViewById<Button>(R.id.add_alarm_button).setOnClickListener { ExternalActionLauncher.addAlarm(this) }
    }

    protected fun saveCurrentLocation(onComplete: (Boolean) -> Unit = {}) =
        validateAddress(save = true, onComplete = onComplete)

    private fun validateAddress(save: Boolean, onComplete: (Boolean) -> Unit = {}) {
        val address = input.text.toString().trim()
        if (address.isEmpty()) {
            status.text = getString(R.string.address_required)
            onComplete(false)
            return
        }
        val request = requestGate.start()
        setWeatherControlsEnabled(false)
        status.text = getString(R.string.testing_address)
        val cached = pendingLocation?.takeIf {
            pendingInput.equals(address, ignoreCase = true) || it.label.equals(address, ignoreCase = true)
        }
        Thread {
            val resolution = cached?.let { AddressResolution.Found(it) } ?: OpenMeteoClient.resolveAddress(address)
            val location = (resolution as? AddressResolution.Found)?.location
            val result = location?.let { OpenMeteoClient.fetch(it, WidgetPreferences.weather(this, activeWidgetId)) }
            runOnUiThread {
                if (!requestGate.isCurrent(request)) return@runOnUiThread
                setWeatherControlsEnabled(true)
                if (location == null) {
                    status.text = getString(when (resolution) {
                        AddressResolution.NotFound -> R.string.address_not_found
                        AddressResolution.NetworkError -> R.string.address_network_error
                        else -> R.string.address_service_error
                    })
                    onComplete(false)
                    return@runOnUiThread
                }
                pendingLocation = location
                pendingInput = location.label
                input.setText(location.label)
                if (result?.fresh == true && result.snapshot != null) {
                    renderPreview(result.snapshot)
                    if (save) {
                        WidgetPreferences.setLocation(this, location, activeWidgetId)
                        WidgetPreferences.setWeather(this, result.snapshot, activeWidgetId)
                        updateActiveWidget()
                        status.text = getString(R.string.address_saved, location.label)
                    } else {
                        status.text = getString(R.string.address_recognized, location.label)
                    }
                    onComplete(true)
                } else {
                    status.text = getString(R.string.weather_validation_failed, location.label)
                    onComplete(false)
                }
            }
        }.start()
    }

    private fun requestLocationFromUser() {
        val granted = checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED ||
            checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
        if (granted) locateDevice() else requestPermissions(
            arrayOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION),
            LOCATION_PERMISSION_REQUEST
        )
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode != LOCATION_PERMISSION_REQUEST) return
        if (grantResults.any { it == PackageManager.PERMISSION_GRANTED }) locateDevice()
        else status.text = getString(R.string.location_permission_denied)
    }

    private fun locateDevice() {
        val request = requestGate.start()
        setWeatherControlsEnabled(false)
        status.text = getString(R.string.locating)
        locator.locate { deviceLocation ->
            if (!requestGate.isCurrent(request)) return@locate
            if (deviceLocation == null) {
                setWeatherControlsEnabled(true)
                status.text = getString(R.string.location_unavailable)
                return@locate
            }
            Thread {
                val location = OpenMeteoClient.reverse(deviceLocation.latitude, deviceLocation.longitude)
                val result = OpenMeteoClient.fetch(location, WidgetPreferences.weather(this, activeWidgetId))
                runOnUiThread {
                    if (!requestGate.isCurrent(request)) return@runOnUiThread
                    setWeatherControlsEnabled(true)
                    pendingLocation = location
                    pendingInput = location.label
                    input.setText(location.label)
                    if (result.fresh && result.snapshot != null) {
                        renderPreview(result.snapshot)
                        status.text = getString(R.string.location_recognized, location.label)
                    } else {
                        status.text = getString(R.string.location_weather_unavailable, location.label)
                    }
                }
            }.start()
        }
    }

    private fun setWeatherControlsEnabled(enabled: Boolean) {
        testAddressButton.isEnabled = enabled
        locationButton.isEnabled = enabled
        refreshButton.isEnabled = enabled
        input.isEnabled = enabled
        findViewById<Button>(R.id.add_widget_button).isEnabled = enabled
    }

    private fun updateActiveWidget() {
        if (activeWidgetId != 0) AppWidgetManager.getInstance(this)
            .updateAppWidget(activeWidgetId, WidgetRenderer.render(this, activeWidgetId))
    }

    protected fun renderPreview(snapshot: WeatherSnapshot? = WidgetPreferences.weather(this, activeWidgetId)) {
        val visual = snapshot?.let { HtcWeatherPresentation.forWmo(it.weatherCode) }
        findViewById<ImageView>(R.id.weather_icon).setImageResource(HtcWeatherDrawable.forCode(visual?.iconCode ?: 7))
        findViewById<TextView>(R.id.temperature).text = snapshot?.let { "${it.temperature}°" } ?: "--°"
        findViewById<TextView>(R.id.city).text = snapshot?.city ?: WidgetPreferences.city(this, activeWidgetId)
        findViewById<TextView>(R.id.condition).text = visual?.label ?: getString(R.string.weather_offline)
        findViewById<TextView>(R.id.range).text = snapshot?.let { "${it.maximum}° / ${it.minimum}°" } ?: "--° / --°"
        findViewById<TextView>(R.id.last_updated).text = snapshot?.let {
            val formatted = SimpleDateFormat("d MMM yyyy 'à' HH:mm", Locale.FRENCH).format(Date(it.updatedAt))
            getString(R.string.last_updated_format, formatted)
        } ?: getString(R.string.no_weather_update)
    }

    private fun requestPinWidget(status: TextView, provider: Class<*> = FlipClockWidgetProvider::class.java) {
        val manager = AppWidgetManager.getInstance(this)
        if (Build.VERSION.SDK_INT >= 26 && manager.isRequestPinAppWidgetSupported) {
            manager.requestPinAppWidget(ComponentName(this, provider), null, null)
            status.text = getString(R.string.confirm_on_launcher)
        } else status.text = getString(R.string.manual_widget_help)
    }

    override fun onDestroy() {
        requestGate.close()
        if (::locator.isInitialized) locator.cancel()
        super.onDestroy()
    }

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

    private companion object { const val LOCATION_PERMISSION_REQUEST = 4102 }
}
