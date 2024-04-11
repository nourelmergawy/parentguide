package com.example.kidscare.navigation.permission.appblocker

import android.app.ActivityManager
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.IBinder
import android.util.Log
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

class AppBlockerService : Service() {
    private var blockedApps = listOf<String>() // Dynamically set list of apps to block
    private var serviceJob: Job? = null

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        // Update blockedApps list from the intent
        blockedApps = intent?.getStringArrayListExtra(BLOCKED_APP_PACKAGES_EXTRA) ?: listOf()

        if (blockedApps.isNotEmpty()) {
            startBlockingApps()
        }

        return START_STICKY
    }

    private fun startBlockingApps() {
        serviceJob?.cancel() // Cancel the previous job if it exists
        serviceJob = CoroutineScope(Dispatchers.Default).launch {
            while (isActive) {
                val foregroundApp = getForegroundApp()
                if (foregroundApp in blockedApps) {
                    launch(Dispatchers.Main) {
                        blockApp(foregroundApp)
                    }
                }
                delay(1000) // Check every second
            }
        }
    }

    private fun getForegroundApp(): String {
        // Logic to get the current foreground app package name
        return "" // Placeholder for actual implementation
    }

    override fun onDestroy() {
        serviceJob?.cancel()
        super.onDestroy()
    }

    companion object {
        const val BLOCKED_APP_PACKAGES_EXTRA = "com.example.kidscare.BLOCKED_APP_PACKAGES_EXTRA"
        const val BLOCKED_APP_NAME_EXTRA = "com.example.kidscare.BLOCKED_APP_NAME_EXTRA"
    }

    private fun blockApp(packageName: String) {
        Log.d("AppBlockerService", "Blocking app: $packageName")
        val blockIntent = Intent(this, BlockedAppActivity::class.java)
        blockIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        blockIntent.putExtra(BLOCKED_APP_NAME_EXTRA, packageName)
        startActivity(blockIntent)
    }

    // Existing code...

    private fun checkIfAppIsBlocked(packageName: String): Boolean {
        // Implement logic to check if the app is running
        val am = getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        val runningAppProcesses = am.runningAppProcesses ?: return false
        return runningAppProcesses.any { it.processName == packageName && it.importance != ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND }
    }
}