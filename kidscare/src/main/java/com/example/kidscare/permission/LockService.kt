package com.example.kidscare.permission

import android.annotation.SuppressLint
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.app.admin.DevicePolicyManager
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.pm.ServiceInfo
import android.os.Build
import android.os.IBinder
import android.os.PowerManager
import android.util.Log
import androidx.core.app.NotificationCompat
import com.example.kidscare.MyDeviceAdminReceiver
import com.example.kidscare.R
import java.util.Timer
import java.util.TimerTask

class LockService : Service() {

    private var devicePolicyManager: DevicePolicyManager? = null
    private var adminComponent: ComponentName? = null
    private val CHANNEL_ID = "ForegroundServiceChannel"

    @SuppressLint("ForegroundServiceType")
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Log.d("LockService", "Service started")

        createNotificationChannel()
        val notification = createNotification()

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            startForeground(1, notification, ServiceInfo.FOREGROUND_SERVICE_TYPE_DATA_SYNC)
        } else {
            startForeground(1, notification)
        }

        startForeground(1, notification)

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
                if (System.currentTimeMillis() - startTime > 180000) {
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

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val serviceChannel = NotificationChannel(
                CHANNEL_ID,
                "Foreground Service Channel",
                NotificationManager.IMPORTANCE_DEFAULT
            )
            val manager = getSystemService(NotificationManager::class.java)
            manager?.createNotificationChannel(serviceChannel)
        }
    }

    private fun createNotification(): Notification {
        return NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("Device Lock Active")
            .setContentText("The device is locked due to a wrong answer.")
            .setSmallIcon(R.drawable.ic_lock) // Ensure this icon exists
            .build()
    }
}
