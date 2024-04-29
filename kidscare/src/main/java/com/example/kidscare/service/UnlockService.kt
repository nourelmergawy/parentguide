package com.example.kidscare.service

import MyFirebaseMessagingService
import android.app.Service
import android.content.Intent
import android.content.IntentFilter
import android.os.Build
import android.os.IBinder

class UnlockService : Service() {

    private lateinit var receiver: ScreenUnlockReceiver
    private val myFirebaseMessagingService = MyFirebaseMessagingService()
    override fun onBind(intent: Intent?): IBinder? {
        return null
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