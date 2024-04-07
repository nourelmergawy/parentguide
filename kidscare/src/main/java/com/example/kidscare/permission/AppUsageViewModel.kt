package com.example.kidscare.permission

import android.app.usage.UsageStatsManager
import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.util.concurrent.TimeUnit

class AppUsageViewModel (private val context: Context): ViewModel() {
    private val _lockScreen = MutableStateFlow(false)
    val lockScreen = _lockScreen.asStateFlow()

    fun checkAppUsageAndLockIfNeeded(packageName: String, maxUsageHours: Int) {
        val usageStatsManager = context.getSystemService(Context.USAGE_STATS_SERVICE) as UsageStatsManager
        val currentTime = System.currentTimeMillis()
        val startTime = currentTime - TimeUnit.DAYS.toMillis(1)  // last 24 hours

        val stats = usageStatsManager.queryUsageStats(UsageStatsManager.INTERVAL_DAILY, startTime, currentTime)
        val appUsageTime = stats.firstOrNull { it.packageName == packageName }?.totalTimeInForeground ?: 0

        if (TimeUnit.MILLISECONDS.toHours(appUsageTime) >= .2) {
            _lockScreen.value = true
        }
    }
    class AppUsageViewModelFactory(private val context: Context) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(AppUsageViewModel::class.java)) {
                @Suppress("UNCHECKED_CAST")
                return AppUsageViewModel(context) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}