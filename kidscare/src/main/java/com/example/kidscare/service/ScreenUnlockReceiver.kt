package com.example.kidscare.service

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.widget.Toast
import com.example.kidscare.UnlockDialogActivity

class ScreenUnlockReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        if (Intent.ACTION_USER_PRESENT == intent.action) {
            val dialogIntent = Intent(context, UnlockDialogActivity::class.java)
            dialogIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            if (Intent.ACTION_USER_PRESENT == intent.action) {
                // Handle the unlock event
                Toast.makeText(context, "Phone unlocked!", Toast.LENGTH_SHORT).show()
            }
            context.startActivity(dialogIntent)
        }
    }
}