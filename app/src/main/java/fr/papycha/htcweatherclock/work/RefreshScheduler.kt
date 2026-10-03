package fr.papycha.htcweatherclock.work

import android.content.Context
import androidx.work.BackoffPolicy
import androidx.work.Constraints
import androidx.work.Data
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.ExistingWorkPolicy
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import java.util.concurrent.TimeUnit

object RefreshScheduler {
    private val constraints = Constraints.Builder().setRequiredNetworkType(NetworkType.CONNECTED).build()

    fun refresh(context: Context, widgetId: Int) {
        val request = OneTimeWorkRequestBuilder<WeatherRefreshWorker>()
            .setInputData(Data.Builder().putInt(WeatherRefreshWorker.KEY_WIDGET_ID, widgetId).build())
            .setConstraints(constraints).setBackoffCriteria(BackoffPolicy.EXPONENTIAL, 30, TimeUnit.SECONDS).build()
        WorkManager.getInstance(context).enqueueUniqueWork("weather-now-$widgetId", ExistingWorkPolicy.REPLACE, request)
    }

    fun periodic(context: Context, widgetId: Int) {
        val request = PeriodicWorkRequestBuilder<WeatherRefreshWorker>(30, TimeUnit.MINUTES)
            .setInputData(Data.Builder().putInt(WeatherRefreshWorker.KEY_WIDGET_ID, widgetId).build())
            .setConstraints(constraints).setBackoffCriteria(BackoffPolicy.EXPONENTIAL, 30, TimeUnit.SECONDS).build()
        WorkManager.getInstance(context).enqueueUniquePeriodicWork("weather-periodic-$widgetId", ExistingPeriodicWorkPolicy.UPDATE, request)
    }

    fun cancel(context: Context, widgetId: Int) {
        WorkManager.getInstance(context).cancelUniqueWork("weather-now-$widgetId")
        WorkManager.getInstance(context).cancelUniqueWork("weather-periodic-$widgetId")
    }
}
