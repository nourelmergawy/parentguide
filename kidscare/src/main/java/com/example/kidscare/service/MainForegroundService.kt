package com.example.kidscare.service

import android.app.Service
import android.content.Intent
import android.os.IBinder
import android.util.Log
import com.example.kidscare.navigation.permission.appblocker.BlockedAppActivity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
class MainForegroundService : Service() {
    private var blockedAppPackageNames: List<String> = emptyList()
    private val coroutineScope = CoroutineScope(Dispatchers.IO)
    private var blockJob: Job? = null

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        blockedAppPackageNames = intent?.getStringArrayListExtra(BLOCKED_APP_PACKAGES_EXTRA) ?: listOf()
        val blockedAppPackageNames = intent?.getStringArrayListExtra(BLOCKED_APP_PACKAGES_EXTRA) ?: listOf()
        Log.d("AppBlockerService", "Blocked apps: $blockedAppPackageNames")
        startAppBlocking()
        return START_STICKY
    }

    private fun startAppBlocking() {
        blockJob?.cancel() // Cancel any existing blocking job
        blockJob = coroutineScope.launch {
            while (isActive) {
                val foregroundApp = getTopAppPackageName()
                if (foregroundApp in blockedAppPackageNames) {
                    blockApp(foregroundApp)
                }
                delay(1000) // Check every second
            }
        }
    }

    private fun getTopAppPackageName(): String {
        // Logic to determine the top app's package name
        // This is platform-dependent and may require querying usage stats
        return ""
    }

    private fun blockApp(packageName: String) {
        // Logic to block the app, such as launching a blocking activity
        val intent = Intent(this, BlockedAppActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        intent.putExtra(BLOCKED_APP_PACKAGES_EXTRA, packageName)
        startActivity(intent)
    }

    override fun onDestroy() {
        blockJob?.cancel()
        coroutineScope.cancel()
        super.onDestroy()
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    companion object {
        const val BLOCKED_APP_PACKAGES_EXTRA = "com.example.kidscare.BLOCKED_APP_PACKAGES_EXTRA"
    }
}