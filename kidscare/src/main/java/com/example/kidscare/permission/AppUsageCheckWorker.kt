package com.example.kidscare.permission
import android.app.usage.UsageStatsManager
import android.content.Context
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.Worker
import androidx.work.WorkerParameters
import java.util.concurrent.TimeUnit

class AppUsageCheckWorker(
    context: Context,
    workerParams: WorkerParameters
) : Worker(context, workerParams) {

    override fun doWork(): Result {
        // Here we would use the actual logic to check app usage and lock the app
        val packageName = "com.whatsapp" // Your app's package name
        val maxUsageHours = .1 // The maximum allowed usage hours

        val usageStatsManager = applicationContext.getSystemService(Context.USAGE_STATS_SERVICE) as UsageStatsManager
        val currentTime = System.currentTimeMillis()
        val startTime = currentTime - TimeUnit.DAYS.toMillis(1)  // Check for the last 24 hours

        val stats = usageStatsManager.queryUsageStats(UsageStatsManager.INTERVAL_DAILY, startTime, currentTime)
        val appUsageTime = stats.firstOrNull { it.packageName == packageName }?.totalTimeInForeground ?: 0

        if (TimeUnit.MILLISECONDS.toHours(appUsageTime) >= maxUsageHours) {
            // Logic to notify or lock the app
            // Since Worker cannot directly interact with UI, you might need to send a broadcast, notification, or use Data to communicate with your UI
        }

        return Result.success()
    }

    fun scheduleAppUsageChecks(context: Context) {
        val workRequest = PeriodicWorkRequestBuilder<AppUsageCheckWorker>(15, TimeUnit.MINUTES) // Adjust the repeat interval as needed
            .build()

        WorkManager.getInstance(context).enqueueUniquePeriodicWork(
            "AppUsageCheck",
            ExistingPeriodicWorkPolicy.KEEP, // or REPLACE depending on your needs
            workRequest
        )
    }
    fun scheduleDeviceLockChecks(context: Context) {
        val workRequest = PeriodicWorkRequestBuilder<AppUsageCheckWorker>(500, TimeUnit.SECONDS) // Adjust the repeat interval as needed
            .build()

        WorkManager.getInstance(context).enqueueUniquePeriodicWork(
            "DeviceLockCheck",
            ExistingPeriodicWorkPolicy.KEEP, // or REPLACE depending on your needs
            workRequest
        )
    }
}