package com.example.kidscare

import android.app.admin.DeviceAdminReceiver
import android.content.Context
import android.content.Intent
import android.widget.Toast

class MyDeviceAdminReceiver : DeviceAdminReceiver() {

    override fun onEnabled(context: Context, intent: Intent) {
        showToast(context, "Device Admin Enabled")
    }

    override fun onDisabled(context: Context, intent: Intent) {
        showToast(context, "Device Admin Disabled")
    }

    override fun onPasswordChanged(context: Context, intent: Intent) {
        showToast(context, "Device password changed")
    }

    // Add more overrides for handling other device admin events as needed

    private fun showToast(context: Context, msg: String) {
        Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
    }
}