package com.example.kidscare.permission
import android.app.admin.DevicePolicyManager
import android.content.ComponentName
import android.content.Context
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.example.kidscare.MyDeviceAdminReceiver

class LockDeviceCheckWorker(
    context: Context,
    workerParams: WorkerParameters
) : Worker(context, workerParams) {

        val  contextworkerParams  = context


    override fun doWork(): Result {
        // Here we would use the actual logic to check app usage and lock the app
        val maxLockMin = 3 // The maximum allowed usage hours

        lockDevice(contextworkerParams)

        return Result.success()
    }



    fun lockDevice(context: Context) {
        val devicePolicyManager = context.getSystemService(Context.DEVICE_POLICY_SERVICE) as DevicePolicyManager
        val adminComponent = ComponentName(context, MyDeviceAdminReceiver::class.java)

        if (devicePolicyManager.isAdminActive(adminComponent)) {
            devicePolicyManager.lockNow()
        }
    }
}