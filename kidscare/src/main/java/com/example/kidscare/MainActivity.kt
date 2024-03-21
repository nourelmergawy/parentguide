package com.example.kidscare

import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.example.kidscare.permission.ApplicationManagerViewModel
import com.example.kidscare.permission.InstalledAppsList
import com.example.kidscare.ui.theme.ParentGuideTheme

class MainActivity : ComponentActivity() {
    private lateinit var viewModel: ApplicationManagerViewModel

    @RequiresApi(Build.VERSION_CODES.UPSIDE_DOWN_CAKE)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = ViewModelProvider(this).get(ApplicationManagerViewModel::class.java)

        // Observe installedApps LiveData
        viewModel.installedApps.observe(this, Observer { installedAppsPair ->
            val (userAppsList, systemAppsList) = installedAppsPair
            // Here you can use the lists of installed apps as needed
            Log.d("MainActivity", "User Apps: $userAppsList")
            Log.d("MainActivity", "System Apps: $systemAppsList")
        })

        // Fetch installed apps
        viewModel.fetchInstalledApps(this)
        setContent {
            ParentGuideTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
//                    MyBottomAppBar( coroutineScope = lifecycleScope)
                    InstalledAppsList(viewModel)
                }
            }
        }
    }

}
