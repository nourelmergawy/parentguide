package com.example.kidscare.permission

import android.app.admin.DevicePolicyManager
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.net.Uri
import com.example.kidscare.MyDeviceAdminReceiver

class AppManager(private val context: Context) {
    private val dpm: DevicePolicyManager = context.getSystemService(Context.DEVICE_POLICY_SERVICE) as DevicePolicyManager
    val adminComponent = ComponentName(context, MyDeviceAdminReceiver::class.java)

    fun isAppDisabled(packageName: String): Boolean {
        if (!dpm.isAdminActive(adminComponent)) {
            return false
        }
        return try {
            dpm.isApplicationHidden(adminComponent, packageName)
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    fun disableApp(packageName: String): Boolean {
        if (!dpm.isAdminActive(adminComponent)) {
            return false
        }

        return try {
            dpm.setApplicationHidden(adminComponent, packageName, true)
        } catch (e: SecurityException) {
            e.printStackTrace()
            false
        }
    }

    fun enableApp(packageName: String): Boolean {
        if (!dpm.isAdminActive(adminComponent)) {
            return false
        }

        return try {
            dpm.setApplicationHidden(adminComponent, packageName, false)
        } catch (e: SecurityException) {
            e.printStackTrace()
            false
        }
    }

    fun installApp(apkUri: Uri) {
        val intent = Intent(Intent.ACTION_VIEW).apply {
            setDataAndType(apkUri, "application/vnd.android.package-archive")
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_GRANT_READ_URI_PERMISSION
        }
        context.startActivity(intent)
    }

    fun uninstallApp(packageName: String) {
        val intent = Intent(Intent.ACTION_UNINSTALL_PACKAGE).apply {
            data = Uri.parse("package:$packageName")
            putExtra(Intent.EXTRA_RETURN_RESULT, true)
            flags = Intent.FLAG_ACTIVITY_NEW_TASK
        }
        context.startActivity(intent)
    }
    fun lockDeviceForDuration() {
        if (dpm.isAdminActive(adminComponent)) {
            // Set the lock screen timeout to 3 minutes (180000 milliseconds)
            dpm.setMaximumTimeToLock(adminComponent, 180000L);

            // Lock the device immediately
            dpm.lockNow();
        }
    }

}
