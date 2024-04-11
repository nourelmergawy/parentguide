package com.example.kidscare.navigation.permission.appblocker

import android.content.Context
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class ApplicationManagerViewModel(): ViewModel() {
    private val _installedApps = MutableLiveData<Pair<List<String>, List<String>>>()
    val installedApps: LiveData<Pair<List<String>, List<String>>> = _installedApps

    fun fetchInstalledApps(context: Context) {
        val packageManager = context.packageManager
        val appsPair = getInstalledApps(packageManager)
        _installedApps.value = appsPair
    }

    private fun getInstalledApps(packageManager: PackageManager): Pair<List<String>, List<String>> {
        val userApps = mutableListOf<String>()
        val systemApps = mutableListOf<String>()
        val packages = packageManager.getInstalledApplications(PackageManager.GET_META_DATA)
        for (packageInfo in packages) {
            val appName = packageInfo.loadLabel(packageManager).toString()
            if (isSystemApp(packageInfo)) {
                systemApps.add(appName)
            } else {
                userApps.add(appName)
            }
        }
        return Pair(userApps, systemApps)
    }

    private fun isSystemApp(applicationInfo: ApplicationInfo): Boolean {
        return applicationInfo.flags and ApplicationInfo.FLAG_SYSTEM != 0
    }
}