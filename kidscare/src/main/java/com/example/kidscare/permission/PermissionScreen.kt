package com.example.kidscare.permission

import android.app.admin.DevicePolicyManager
import android.content.ComponentName
import android.content.ContentValues.TAG
import android.content.Context
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import android.graphics.drawable.Drawable
import android.os.Build
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.activity.compose.BackHandler
import androidx.annotation.RequiresApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.core.graphics.drawable.toBitmap
import com.example.kidscare.MyDeviceAdminReceiver
import com.example.kidscare.R

@RequiresApi(Build.VERSION_CODES.UPSIDE_DOWN_CAKE)
@Composable
fun InstalledAppsList(
    viewModel: ApplicationManagerViewModel,
    context: Context,
    appUsageViewModel: AppUsageViewModel,) {

//    val context = LocalContext.current
    // Create an instance of AppManager with the context
    val appManager = AppManager(context)
    val packageManager = remember { context.packageManager }
    val installedApps = remember {
        packageManager.getInstalledApplications(PackageManager.GET_META_DATA)
            .filter { it.packageName != context.packageName }
            .filterNot { isSystemApp(it) }
    }

    // State to hold selected apps
    val selectedApps = remember { mutableStateListOf<ApplicationInfo>() }

    Column (
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ){
        Button(onClick = {
            Log.d(TAG, "isAppDisabled:${appManager.isAppDisabled(selectedApps.get(0).packageName)}")
//            appManager.disableApp(selectedApps.get(0).packageName)
            appManager.lockDeviceForDuration()
            Log.d(TAG, "InstalledAppsList:${selectedApps.get(0).packageName}")
            Log.d(TAG, "isDeviceAdminActive: ${isDeviceAdminActive(context = context)}")
// Wait a moment before checking the disabled status again
            Handler(Looper.getMainLooper()).postDelayed({
                Log.d(TAG, "isAppDisabled:${appManager.isAppDisabled(selectedApps.get(0).packageName)}")
            }, 1000)  // Delay of 1 second

        }) {
            Text(text = "Block Selected Apps")
        }

        Row (modifier = Modifier.padding(8.dp)){
                    LazyColumn {
                        items(installedApps) { app ->
                            AppItem(app, context, selectedApps)
                        }

                    }
                }

        }

    BackHandler {
        // Handle back button if needed
    }
}
@Composable
fun AppItem(
    appInfo: ApplicationInfo,
    context: Context,
    selectedApps: MutableList<ApplicationInfo>
) {
    val icon = getAppIcon(context, appInfo.packageName)
    val appName = getAppName(context, appInfo.packageName)

    var isChecked by remember { mutableStateOf(selectedApps.contains(appInfo)) }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
            .clickable {
                isChecked = !isChecked
                if (isChecked) {
                    selectedApps.add(appInfo)
                } else {
                    selectedApps.remove(appInfo)
                }
            }
    ) {
        Spacer(modifier = Modifier.width(16.dp))
        AppIcon(icon)
        Spacer(modifier = Modifier.width(16.dp))
        Column {
            Text(text = appName ?: "Unknown App", color = Color.Black)
        }
        Switch(
            checked = isChecked,
            onCheckedChange = {
                isChecked = it
                if (isChecked) {
                    selectedApps.add(appInfo)
                } else {
                    selectedApps.remove(appInfo)
                }
            }
        )
    }
}


fun getAppName(context: Context, packageName: String): String? {
    return try {
        val appInfo = context.packageManager.getApplicationInfo(packageName, PackageManager.GET_META_DATA)
        val packageManager = context.packageManager
        val appName = packageManager.getApplicationLabel(appInfo)
        appName.toString()
    } catch (e: PackageManager.NameNotFoundException) {
        null
    }
}

@Composable
fun AppIcon(icon: Drawable?) {
    val bitmap = icon?.toBitmap()?.asImageBitmap()

    if (bitmap != null) {
        Image(
            bitmap = bitmap,
            contentDescription = null,
            modifier = Modifier.size(48.dp)
        )
    } else {
        // Placeholder icon if the app icon is not available
        Image(
            painter = painterResource(id = R.drawable.ic_launcher_foreground),
            contentDescription = null,
            modifier = Modifier.size(48.dp)
        )
    }
}

fun getAppIcon(context: Context, packageName: String): Drawable? {
    return try {
        val iconDrawable = context.packageManager.getApplicationIcon(packageName)
        iconDrawable
    } catch (e: PackageManager.NameNotFoundException) {
        null
    }
}

fun isSystemApp(appInfo: ApplicationInfo): Boolean {
    return (appInfo.flags and ApplicationInfo.FLAG_SYSTEM) != 0
}
fun isDeviceAdminActive(context: Context): Boolean {
    val devicePolicyManager = context.getSystemService(Context.DEVICE_POLICY_SERVICE) as DevicePolicyManager
    val adminComponent = ComponentName(context, MyDeviceAdminReceiver::class.java)
    return devicePolicyManager.isAdminActive(adminComponent)
}
@Composable
fun AppLockScreen() {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Text("Usage Limit Reached")
    }
}

