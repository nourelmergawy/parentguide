package com.example.kidscare.permission

import android.content.Context
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import android.graphics.drawable.Drawable
import android.os.Build
import androidx.activity.compose.BackHandler
import androidx.annotation.RequiresApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.core.graphics.drawable.toBitmap
import com.example.kidscare.R

@RequiresApi(Build.VERSION_CODES.UPSIDE_DOWN_CAKE)
@Composable
fun InstalledAppsList(viewModel: ApplicationManagerViewModel) {
    val context = LocalContext.current
    val packageManager = remember { context.packageManager }
    val installedApps = remember {
        packageManager.getInstalledApplications(PackageManager.GET_META_DATA)
            .filter { it.packageName != context.packageName }
            .filterNot { isSystemApp(it) }
    }

    // State to hold selected apps
    val selectedApps = remember { mutableStateListOf<ApplicationInfo>() }

    // State to show dialog
    val showDialog = remember { mutableStateOf(false) }

    Column {
        Row(){
            // Select All button
            Button(onClick = { selectedApps.addAll(installedApps) }) {
                Text(text = "Select All")
            }

            // Deselect All button
            Button(onClick = { selectedApps.clear() }) {
                Text(text = "Deselect All")
            }
        }

        // Button to display selected apps
        Button(onClick = { showDialog.value = true }) {
            Text(text = "Show Selected Apps (${selectedApps.size})")
        }

        // App list
        LazyColumn {
            items(installedApps) { app ->
                AppItem(app, context, selectedApps)
            }
        }


        // Dialog to display selected apps
        if (showDialog.value) {
            AlertDialog(
                onDismissRequest = { showDialog.value = false },
                title = { Text(text = "Selected Apps") },
                text = {
                    Column {
                        selectedApps.forEach { app ->
                            Text(text = app.packageName)
                        }
                    }
                },
                confirmButton = {
                    Button(onClick = { showDialog.value = false }) {
                        Text(text = "OK")
                    }
                }
            )
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
    val icon = remember(appInfo.packageName) {
        getAppIcon(context, appInfo.packageName)
    }

    val appName = remember(appInfo.packageName) {
        getAppName(context, appInfo.packageName)
    }

    // Checkbox to select/deselect app
    val isChecked = selectedApps.contains(appInfo)
    val toggleChecked: () -> Unit = {
        if (isChecked) {
            selectedApps.remove(appInfo)
        } else {
            selectedApps.add(appInfo)
        }
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
            .clickable(onClick = toggleChecked)
    ) {
        Checkbox(
            checked = isChecked,
            onCheckedChange = { toggleChecked() }
        )
        Spacer(modifier = Modifier.width(16.dp))
        AppIcon(icon)
        Spacer(modifier = Modifier.width(16.dp))
        Column {
            Text(text = appName ?: "Unknown App", color = Color.Black)
        }
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
