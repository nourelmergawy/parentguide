package com.example.kidscare.navigation.permission.lockdevice

import android.annotation.SuppressLint
import android.app.Service
import android.app.admin.DevicePolicyManager
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.IBinder
import android.os.PowerManager
import android.util.Log
import com.example.kidscare.service.MyDeviceAdminReceiver
import java.util.Timer
import java.util.TimerTask

class LockService : Service() {

    private var devicePolicyManager: DevicePolicyManager? = null
    private var adminComponent: ComponentName? = null
    private var timer: Timer? = null

    @SuppressLint("ForegroundServiceType")
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Log.d("LockService", "Service started")


        devicePolicyManager = getSystemService(Context.DEVICE_POLICY_SERVICE) as DevicePolicyManager
        adminComponent = ComponentName(this, MyDeviceAdminReceiver::class.java)

        // Immediately lock the device if admin is active
        if (devicePolicyManager?.isAdminActive(adminComponent!!) == true) {
            devicePolicyManager?.lockNow()
        }

        val timer = Timer()
        timer.schedule(object : TimerTask() {
            val startTime = System.currentTimeMillis()

            override fun run() {
                // Check if 3 minutes have elapsed
                if (System.currentTimeMillis() - startTime > 72000) {
                    timer.cancel()  // Stops the Timer
                    timer.purge()   // Removes all cancelled tasks from the timer's task queue
                    return
                }

                // If the device is unlocked, re-lock it
                if (!isScreenOff(this@LockService)) {
                    if (devicePolicyManager?.isAdminActive(adminComponent!!) == true) {
                        devicePolicyManager?.lockNow()
                    }
                }
            }
        }, 0, 10000) // Start immediately, check every 10 seconds
        return START_STICKY
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    private fun isScreenOff(context: Context): Boolean {
        val powerManager = context.getSystemService(Context.POWER_SERVICE) as PowerManager
        return !powerManager.isInteractive
    }
    fun unlockDeviceForTwoHours(context: Context) {
        val unlockDuration = 2 * 60 * 60 * 1000 // 2 hours in milliseconds
        val intent = Intent(context, LockService::class.java).apply {
            putExtra("unlockDuration", unlockDuration)
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            context.startForegroundService(intent)
        } else {
            context.startService(intent)
        }

    }

}
