package com.example.kidscare.service


import ParentGuideApp
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.widget.Toast

class ScreenUnlockReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == Intent.ACTION_USER_PRESENT) {
            // Phone screen is unlocked
            ParentGuideApp().showParentDialog(context)
        }
    }
}
