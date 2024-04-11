package com.example.kidscare.permission.appblocker

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.kidscare.R
import com.example.kidscare.permission.appblocker.AppBlockerService.Companion.BLOCKED_APP_NAME_EXTRA
import com.example.kidscare.service.MainForegroundService.Companion.BLOCKED_APP_PACKAGES_EXTRA

class BlockedAppActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val blockedAppName = intent.getStringExtra(BLOCKED_APP_PACKAGES_EXTRA) ?: ""

        setContent {
            BlockedAppScreen(blockedAppName)

            val blockedAppName2 = intent.getStringExtra(BLOCKED_APP_NAME_EXTRA)
            Log.d("BlockedAppActivity", "Blocked app opened: $blockedAppName2")
        }
    }
}

@Composable
fun BlockedAppScreen(blockedAppName: String) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Image(
            painter = painterResource(R.drawable.ic_cancel),
            contentDescription = "Blocked",
            modifier = Modifier.size(64.dp)
        )
        Text(
            text = blockedAppName,
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier.padding(4.dp)
        )
        Text(
            text = stringResource(R.string.this_app_is_blocked_by_your_parents),
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.padding(4.dp)
        )
    }
}
