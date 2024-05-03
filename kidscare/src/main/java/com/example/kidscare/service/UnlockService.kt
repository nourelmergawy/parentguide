package com.example.kidscare.service

import MyFirebaseMessagingService
import android.R
import android.app.Notification
import android.app.Service
import android.content.Intent
import android.content.IntentFilter
import android.os.Build
import android.os.IBinder
import androidx.core.app.NotificationCompat


class UnlockService : Service() {

    private lateinit var receiver: ScreenUnlockReceiver
    private val myFirebaseMessagingService = MyFirebaseMessagingService()
    override fun onBind(intent: Intent?): IBinder? {
        return null
    }
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        // Your service initialization code
        // Call startForeground within this method

        startForeground(1, myFirebaseMessagingService.showNotification(this,"test"))

        return START_STICKY
    }
    override fun onCreate() {
        super.onCreate()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            myFirebaseMessagingService.showNotification(this,"test")

        }else{
            myFirebaseMessagingService.showNotification(this,"")

        }

        receiver = ScreenUnlockReceiver()
        val filter = IntentFilter(Intent.ACTION_USER_PRESENT)
        receiver.onReceive(this, Intent(Intent.ACTION_USER_PRESENT))
        registerReceiver(receiver, filter)
    }

    override fun onDestroy() {
        super.onDestroy()
        unregisterReceiver(receiver)
    }

    companion object {
        const val CHANNEL_ID = "ForegroundServiceChannel"
    }
}