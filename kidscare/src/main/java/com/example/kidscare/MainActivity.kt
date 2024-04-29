package com.example.kidscare

import android.content.BroadcastReceiver
import android.content.ContentValues.TAG
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.text.format.DateFormat
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.kidscare.navigation.permission.scenarioViewModel
import com.example.kidscare.service.UserPresentReceiver
import com.example.kidscare.signin.GoogleAuthUiClient
import com.example.kidscare.signin.SignInScreen
import com.example.kidscare.signin.SignInViewModel
import com.example.kidscare.ui.theme.ParentGuideTheme
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {

    private val googleSignInOptions by lazy {
        GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestEmail()
            .requestIdToken("480566397634-6dhd36530p3j54f332sumclue4kgdlfe.apps.googleusercontent.com")
            .build()
    }

    private val googleSignInClient by lazy {
        GoogleSignIn.getClient(applicationContext, googleSignInOptions)
    }


    private val googleAuthUiClient by lazy {
        GoogleAuthUiClient(applicationContext)
    }


//    private val userPresentReceiver = UserPresentReceiver()
    private lateinit var userPresentReceiver: BroadcastReceiver
    private val viewModel: scenarioViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val dateFormat = DateFormat.getDateFormat(
            applicationContext
        )
        // Initialize userPresentReceiver before registering
        userPresentReceiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context?, intent: Intent?) {
                if (intent?.action == Intent.ACTION_USER_PRESENT) {
                    viewModel.onUserPresent()
                }
            }
        }

        // Register for ACTION_USER_PRESENT
        val userPresentFilter = IntentFilter(Intent.ACTION_USER_PRESENT)
        registerReceiver(userPresentReceiver, userPresentFilter)
        if (intent.getBooleanExtra("triggerDialog", false)) {
            viewModel.onUserPresent()
        }
        // Set the content view for the activity
        setContent {
            ParentGuideTheme {
                // Initialize the receiver
                MyApp()
//                AppContent()
            }
        }
    }
//    override fun onDestroy() {
//        super.onDestroy()
//        unregisterReceiver(userPresentReceiver)
//    }
    override fun onStart() {
        super.onStart()
        userPresentReceiver = UserPresentReceiver()
        val filter = IntentFilter(Intent.ACTION_USER_PRESENT)
        registerReceiver(userPresentReceiver, filter)
    }

    override fun onStop() {
        super.onStop()
        unregisterReceiver(userPresentReceiver)
    }
    @Composable
    fun MyApp(viewModel: scenarioViewModel = viewModel()) {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background
        ) {
            Box {
                ShowDialogIfNeeded(viewModel)
                // Additional UI components can be placed here
                MainContent()
            }
        }
    }

    @Composable
    fun ShowDialogIfNeeded(viewModel: scenarioViewModel) {
        // Ensure that viewModel.showDialog is a StateFlow and initialize collectAsState properly
        val showDialog by viewModel.showDialog.collectAsState()

        if (showDialog) {
            AlertDialog(
                onDismissRequest = { viewModel.dismissDialog() },
                title = { Text("Welcome Back!") },
                text = { Text("You've just unlocked your phone. Have fun!") },
                confirmButton = {
                    Button(onClick = { viewModel.dismissDialog() }) {
                        Text("OK")
                    }
                }
            )
        }
    }

    @Composable
    fun MainContent() {
        // Here, you can define the main content of your app
        Text(text = "Hello, this is the main content of the app!", style = MaterialTheme.typography.bodyMedium)
    }



    @Composable
    private fun AppContent() {
        val navController = rememberNavController()
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background
        ) {
            NavHost(navController = navController, startDestination = "sign_in") {
                composable("sign_in") {
                    SignInUI()
                }
            }
        }
    }

    @Composable
    private fun SignInUI() {
        val viewModel: SignInViewModel = viewModel()
        val state by viewModel.state.collectAsStateWithLifecycle()

        LaunchedEffect(key1 = Unit) {
            if (googleAuthUiClient.getSignedInUser() != null) {
                navigateToMain()
            }
        }

        val launcher = rememberLauncherForActivityResult(
            contract = ActivityResultContracts.StartIntentSenderForResult(),
            onResult = { result ->
                if (result.resultCode == RESULT_OK) {
                    lifecycleScope.launch {
                        val signInResult = googleAuthUiClient.signInWithIntent(
                            intent = result.data ?: return@launch
                        )
                        viewModel.onSignInResult(signInResult)
                    }
                }
            }
        )

        LaunchedEffect(key1 = state.isSignInSuccessful) {
            if (state.isSignInSuccessful) {
                showToast("Sign in successful")
                Log.d(TAG, "navigateToMain: MainActivity2::class.java")

                navigateToMain()
            }
        }

        SignInScreen(
            state = state,
            onSignInClick = {
                lifecycleScope.launch {
                    val signInIntentSender = googleAuthUiClient.signIn()
                    signInIntentSender?.let {
                        launcher.launch(
                            IntentSenderRequest.Builder(it).build()
                        )
                    }
                }
            }
        )
    }

    private fun navigateToMain() {
        Log.d(TAG, "navigateToMain: MainActivity2::class.java")
        startActivity(Intent(this, MainActivity2::class.java))
        finish()
    }

    private fun showToast(message: String) {
        Toast.makeText(applicationContext, message, Toast.LENGTH_LONG).show()
    }

}
