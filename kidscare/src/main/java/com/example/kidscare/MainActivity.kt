package com.example.kidscare

import android.content.ContentValues.TAG
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.kidscare.service.UnlockReceiver
import com.example.kidscare.signin.GoogleAuthUiClient
import com.example.kidscare.signin.SignInScreen
import com.example.kidscare.signin.SignInViewModel
import com.example.kidscare.ui.theme.ParentGuideTheme
import com.example.kidscare.unlockDialog.UnlockDialog
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    private lateinit var unlockReceiver: UnlockReceiver
    private var showDialog by mutableStateOf(false)
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



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
//test
            ParentGuideTheme {
                if (showDialog) {
                    Log.d("Compose", "Dialog should show now")
                }
                UnlockDialog(showDialog, onDismiss = { showDialog = false })
                InitializeReciver()

                // Ensure to unregister the receiver when the activity is destroyed
                lifecycle.addObserver(LifecycleEventObserver { _, event ->
                    if (event == Lifecycle.Event.ON_DESTROY) {
                        unregisterReceiver(unlockReceiver)
                    }
                })

                AppContent()
            }
        }
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
    fun InitializeReciver() {
        // Initialize and register receiver
        unlockReceiver = UnlockReceiver { showDialog = true }
        IntentFilter(Intent.ACTION_USER_PRESENT).also {
            registerReceiver(unlockReceiver, it)
        }


    }
}
