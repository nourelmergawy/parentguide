package com.example.kidscare.service

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import com.example.kidscare.MainActivity

class UserPresentReceiver : BroadcastReceiver() {

        override fun onReceive(context: Context, intent: Intent?) {
                Log.d("UserUnlockReceiver", "Received unlock event")
                if (intent?.action == Intent.ACTION_USER_PRESENT) {
                    val intent = Intent(context, MainActivity::class.java).apply {
                        flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
                        putExtra("triggerDialog", true)
                    }
                    context.startActivity(intent)
                }
            }
        }