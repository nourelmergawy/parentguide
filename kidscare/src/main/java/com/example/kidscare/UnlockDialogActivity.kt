package com.example.kidscare

import android.app.ActivityManager
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.activity.compose.setContent
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.kidscare.service.UnlockService

class UnlockDialogActivity : AppCompatActivity() {

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            DialogContent(onDismiss = { finish() })
            val serviceIntent = Intent(this, UnlockService::class.java)
            startForegroundService(serviceIntent)
            val isMyServiceRunning = isServiceRunning(this, UnlockService::class.java)
            Toast.makeText(this, "Service running: $isMyServiceRunning", Toast.LENGTH_SHORT).show()
        }
    }
    @Composable
    fun DialogContent(onDismiss: () -> Unit) {
        Dialog(
            onDismissRequest = onDismiss,
            properties = DialogProperties(
                dismissOnBackPress = true,
                dismissOnClickOutside = true
            )
        ) {
            // Dialog content
            Surface(
                modifier = Modifier.width(300.dp),
                color = androidx.compose.ui.graphics.Color.White,
                shape = MaterialTheme.shapes.medium
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text("Welcome back!", style = MaterialTheme.typography.bodyMedium, modifier = Modifier.padding(16.dp))
                    Button(
                        onClick = onDismiss,
                        modifier = Modifier.padding(16.dp)
                    ) {
                        Text("OK")
                    }
                }
            }
        }
    }

    fun isServiceRunning(context: Context, serviceClass: Class<*>): Boolean {
        val manager = context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        for (service in manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.name == service.service.className) {
                return true
            }
        }
        return false
    }
}