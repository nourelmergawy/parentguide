package com.example.kidscare

import android.app.ActivityManager
import android.app.admin.DevicePolicyManager
import android.content.ComponentName
import android.content.ContentValues.TAG
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import coil.compose.AsyncImage
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.rememberLottieComposition
import com.example.kidscare.Models.Scenario
import com.example.kidscare.navigation.permission.lockdevice.LockService
import com.example.kidscare.service.MyDeviceAdminReceiver
import com.example.kidscare.unlockDialog.ScenarioViewModel

class UnlockDialogActivity : AppCompatActivity() {
    private lateinit var scenarioViewModel: ScenarioViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            val showDialog = remember { mutableStateOf(true) }
            val showInteractiveScenario = remember { mutableStateOf(false) }

            scenarioViewModel = ViewModelProvider(this)[ScenarioViewModel::class.java]

            // Observe LiveData to update UI.
            val scenarioData by scenarioViewModel._scenarios.observeAsState(initial = null)

            // Debugging: Check if data is received.
            LaunchedEffect(key1 = scenarioData) {
                scenarioViewModel.getAllScenarios() // Ensure this is called at the right place.

                if (scenarioData != null) {
                    Log.d(TAG, "Scenarios loaded: $scenarioData")
                } else {
                    Log.d(TAG, "Waiting for scenarios...")
                }
            }

            scenarioData?.let { scenario ->
                Log.d(TAG, "onCreate: $scenario")
                Column (){
                    AsyncImage(
                        model = R.drawable.senario2,
                        contentDescription = "App permissions ",
                        modifier = Modifier
                            .fillMaxSize()
                    )
                    if (showDialog.value) {
                        BottomDialog(
                            showDialog = showDialog.value,
                            onDismissRequest = { showDialog.value = false },
                            scenario = scenario,
                            onDialogComplete = {
                                showDialog.value = false
                                showInteractiveScenario.value = true
                            }
                        )
                    } else if (showInteractiveScenario.value) {
                        InteractiveScenario()
                    }
                }

            } ?: Text("Loading...")

        }
    }

}
@Composable
fun BottomDialog(
    showDialog: Boolean,
    onDismissRequest: () -> Unit,
    scenario: List<Scenario>,
    onDialogComplete: () -> Unit
) {
    if (showDialog) {
        Dialog(onDismissRequest = onDismissRequest) {
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp),
            ) {
                MainDialog(scenario.get(0), onDialogComplete)
            }
        }
    }
}

@Composable
fun MainDialog(scenarios: Scenario, onDialogComplete: () -> Unit) {
    var stage by remember { mutableStateOf(0) }
    var showDialog by remember { mutableStateOf(true) }  // State to control dialog visibility

    if (!showDialog) {
        // Optionally reset state or handle other cleanup actions
        onDialogComplete()
        return  // Exit the Composable when the dialog is not shown
    }

    Dialog(onDismissRequest = { showDialog = false }) {
            Column(modifier = Modifier.padding(16.dp)
                .background(Color.White)) {
                Text(scenarios.title, )

                // Display the content based on the current stage
                when (stage) {
                    0 -> Text(
                        text = scenarios.content,

                    )

                    1 -> scenarios.answers.forEach { answer ->
                        Text(text = answer, )
                    }

                    2 -> Text(
                        text = scenarios.recommended,

                        color = Color.Green
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                Button(onClick = {
                    if (stage < 2) {
                        stage++  // Move to the next stage of the current scenario
                    } else {

                            showDialog = false  // Close the dialog after the last scenario
                    }
                }) {
                    Text("Next",)
                }

            }
        }
    }
@Composable
fun InteractiveScenario() {
    var selectedOption by remember { mutableStateOf("") }
    var showDialog by remember { mutableStateOf(true) }
    var showFeedbackDialog by remember { mutableStateOf(false) }
    var feedbackMessage by remember { mutableStateOf("") }
    val context =  LocalContext.current
    if (showDialog) {
        Dialog(onDismissRequest = { showDialog = false }) {
            Column(modifier = Modifier.padding(16.dp).background(Color.White)) {
                Text(
                    "What would you do if you received a suspicious link?",
                    style = MaterialTheme.typography.bodyMedium
                )
                Spacer(modifier = Modifier.height(16.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Button(
                        onClick = {
                            selectedOption = "Click on link"
                            feedbackMessage = "Incorrect action. Do not click on suspicious links!"
                            showFeedbackDialog = true
                            val serviceIntent = Intent(context, LockService::class.java)
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                                ContextCompat.startForegroundService(context, serviceIntent)
                            } else {
                                context.startService(serviceIntent)
                            }

                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Color.Red)
                    ) {
                        Text(text = "Click on link")
                    }
                    Button(
                        onClick = {
                            selectedOption = "Ignore the link"
                            feedbackMessage = "Correct! Always ignore suspicious links."
                            showFeedbackDialog = true
                            if (isDeviceAdminActive(context)) {
                                lockDeviceForTwoHours(context)
                            } else {
                                Toast.makeText(context, "Device admin not enabled", Toast.LENGTH_SHORT).show()
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Color.Green)
                    ) {
                        Text(text = "Ignore the link")
                    }
                }
                if (selectedOption.isNotEmpty()) {
                    Text("You selected: $selectedOption", style = MaterialTheme.typography.bodyMedium)
                }
            }
        }
    }

    if (showFeedbackDialog) {
        FeedbackDialog(
            message = feedbackMessage,
            onDismiss = {
                showFeedbackDialog = false
                showDialog = false // Optionally close the main dialog
                if (selectedOption == "Ignore the link") {
                    // Unlock the device for 2 hours
                    lockDeviceForTwoHours(context)
                }
            }
        )
    }
}

@Composable
fun FeedbackDialog(message: String, onDismiss: () -> Unit) {
    Dialog(onDismissRequest = { onDismiss() }) {
        Column(
            modifier = Modifier
                .padding(16.dp)
                .background(Color.White),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(message, style = MaterialTheme.typography.bodyMedium)
            Spacer(modifier = Modifier.height(16.dp))
            Button(onClick = { onDismiss() }) {
                Text("OK")
            }
        }
    }
}

fun lockDeviceForTwoHours(context: Context) {
//    val lockService =LockService()
//    lockService.unlockDeviceForTwoHours(context)
    val serviceIntent = Intent(context, LockService::class.java)
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        ContextCompat.startForegroundService(context, serviceIntent)
    } else {
        context.startService(serviceIntent)
    }
}
fun isDeviceAdminActive(context: Context): Boolean {
    val devicePolicyManager = context.getSystemService(Context.DEVICE_POLICY_SERVICE) as DevicePolicyManager
    val componentName = ComponentName(context, MyDeviceAdminReceiver::class.java)
    return devicePolicyManager.isAdminActive(componentName)
}
    @Composable
    fun lotteQuizAnimation(answer: Boolean) {

        if (answer) {
            val composition by rememberLottieComposition(LottieCompositionSpec.Asset("correct.json"))
            LottieAnimation(
                composition = composition,
                iterations = Int.MAX_VALUE,
                modifier = Modifier.fillMaxWidth(),
                contentScale = ContentScale.FillWidth

            )
        } else {
            val composition by rememberLottieComposition(LottieCompositionSpec.Asset("wrong.json"))
            LottieAnimation(
                composition = composition,
                iterations = Int.MAX_VALUE,
                modifier = Modifier.size(400.dp),
                alignment = Alignment.Center,
                contentScale = ContentScale.FillWidth
            )
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