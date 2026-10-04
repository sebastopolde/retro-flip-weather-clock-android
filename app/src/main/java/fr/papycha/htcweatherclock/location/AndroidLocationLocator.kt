package fr.papycha.htcweatherclock.location

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Bundle
import android.os.Handler
import android.os.Looper

class AndroidLocationLocator(private val context: Context) {
    private val manager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
    private val handler = Handler(Looper.getMainLooper())
    private var listener: LocationListener? = null
    private var fallback: Location? = null
    private var completed = false

    @Suppress("MissingPermission", "DEPRECATION")
    fun locate(callback: (Location?) -> Unit) {
        cancel()
        completed = false
        val hasFinePermission = context.checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) ==
            PackageManager.PERMISSION_GRANTED
        val providers = LocationProviderSpec.providers(hasFinePermission)
            .filter { runCatching { manager.isProviderEnabled(it) }.getOrDefault(false) }
        fallback = providers.mapNotNull { runCatching { manager.getLastKnownLocation(it) }.getOrNull() }
            .maxByOrNull { it.time }
        if (fallback != null && System.currentTimeMillis() - fallback!!.time < FRESH_LOCATION_MS) {
            complete(fallback, callback)
            return
        }
        if (providers.isEmpty()) {
            complete(fallback, callback)
            return
        }
        val updates = object : LocationListener {
            override fun onLocationChanged(location: Location) = complete(location, callback)
            override fun onProviderEnabled(provider: String) = Unit
            override fun onProviderDisabled(provider: String) = Unit
            @Deprecated("Legacy Android callback")
            override fun onStatusChanged(provider: String?, status: Int, extras: Bundle?) = Unit
        }
        listener = updates
        val registered = providers.map { provider ->
            runCatching {
                manager.requestLocationUpdates(provider, 0L, 0f, updates, Looper.getMainLooper())
                true
            }.getOrDefault(false)
        }.any { it }
        if (registered) {
            handler.postDelayed({ complete(fallback, callback) }, TIMEOUT_MS)
        } else complete(fallback, callback)
    }

    fun cancel() {
        listener?.let { manager.removeUpdates(it) }
        listener = null
        handler.removeCallbacksAndMessages(null)
    }

    private fun complete(location: Location?, callback: (Location?) -> Unit) {
        if (completed) return
        completed = true
        cancel()
        callback(location)
    }

    private companion object {
        const val FRESH_LOCATION_MS = 15 * 60 * 1000L
        const val TIMEOUT_MS = 12_000L
    }
}
