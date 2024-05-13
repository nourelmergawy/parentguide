package com.example.kidscare

import android.annotation.SuppressLint
import android.app.Activity
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
import androidx.compose.ui.window.DialogProperties
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
        if (isActivityDisabled()) {
            Toast.makeText(this, "This activity is disabled for 2 hours.", Toast.LENGTH_LONG).show()
            finish()
            return
        }

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
                Column {
                    val drawableList = listOf(
                        R.drawable.senario1,  // Assume these are valid drawable resources
                        R.drawable.senario2,
                        R.drawable.senario3
                    )

                    // Safe access to drawable list, adjust indices appropriately
                    val imageIndex = when(scenario.get(0).title){
                        " Suspicious Text Message" -> 0
                        "Suspicious Link" -> 1
                        else -> 2
                    }
                    AsyncImage(
                        model = if (drawableList[imageIndex] != null) {
                            drawableList[imageIndex]
                        }else {
                            R.drawable.senario1
                        }
                        ,  // Use imageIndex safely
                        contentDescription = "App permissions",
                        modifier = Modifier.fillMaxSize()
                    )
                }
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
                        InteractiveScenarios(scenarios = scenario)

                    }


            } ?: Text("Loading...")

        }
    }
    private fun isActivityDisabled(): Boolean {
        val sharedPreferences = getSharedPreferences("UnlockDialogActivity", Context.MODE_PRIVATE)
        val disableUntil = sharedPreferences.getLong("disableUntil", 0)
        return System.currentTimeMillis() < disableUntil
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
            Column {
                Dialog(onDismissRequest = { showDialog = false }) {
                    Column(modifier = Modifier
                        .padding(16.dp)
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

        }
@Composable
fun InteractiveScenarios(scenarios: List<Scenario>) {
    var currentScenarioIndex by remember { mutableStateOf(0) }
    var showDialog by remember { mutableStateOf(true) }
    var showFeedbackDialog by remember { mutableStateOf(false) }
    var showMainDialog by remember { mutableStateOf(false) }
    var feedbackMessage by remember { mutableStateOf("") }
    val context = LocalContext.current

    // Function to handle feedback and determine the next steps
    fun handleFeedback(isCorrect: Boolean) {
        if (isCorrect) {
            feedbackMessage = "Correct! "
            showDialog = false  // Dismiss the initial dialog when the action is correct
        } else {
            feedbackMessage = "Incorrect action. "
            if (currentScenarioIndex < scenarios.size - 1) {
                currentScenarioIndex++  // Move to the next scenario if incorrect
            }
        }
        showFeedbackDialog = true
    }

    // Interactive scenario dialog
    if (showDialog && scenarios.isNotEmpty()) {
        val currentScenario = scenarios[currentScenarioIndex]
        Dialog(
            onDismissRequest = { showDialog = false },
            properties = DialogProperties(
                usePlatformDefaultWidth = false  // This allows more control over the Dialog styling
            )
        ) {
            // The outermost container with a transparent background
            Surface(
                modifier = Modifier

                    .background(Color.Transparent),  // Set Surface background to transparent
                color = Color.Transparent,  // Ensure the surface itself is transparent
                shape = RoundedCornerShape(0.dp)  // Optional: you can set the corner shape if needed
            ) {
                // Content inside the dialog
                ScenarioDialogContent(currentScenario, onPositiveClick = {
                    handleFeedback(false)
                    showMainDialog = true  // Prepare to show MainDialog
                }, onNegativeClick = {
                    handleFeedback(true)
                })
            }
        }

    }

    // Feedback dialog for results of user's actions
    if (showFeedbackDialog) {
        Dialog(onDismissRequest = { showFeedbackDialog = false }) {
            FeedbackDialogContent(feedbackMessage, onDismiss = {
                showFeedbackDialog = false
                if (feedbackMessage.startsWith("Incorrect")) {
                    showMainDialog = true  // Show MainDialog if the action was incorrect
                }
            })
        }
    }

    // Main dialog showing more details or actions
    if (showMainDialog) {
        Column {

            // Ensure we do not exceed the bounds of drawableList and scenarios
            if (currentScenarioIndex < scenarios.size) {
                // Display the main dialog with the current scenario

                MainDialog(scenarios[currentScenarioIndex], onDialogComplete = {
                    showMainDialog = false  // Hide MainDialog once completed
                })
            }
        }
    } else if (currentScenarioIndex >= scenarios.size) {
        // Correct condition to avoid incorrect scenario indexing
        showMainDialog = false
    }
}

@Composable
fun ScenarioDialogContent(scenario: Scenario, onPositiveClick: () -> Unit, onNegativeClick: () -> Unit) {
    val context = LocalContext.current
    Column(modifier = Modifier
        .padding(16.dp)

        , verticalArrangement = Arrangement.Bottom) {
        Text(scenario.content, style = MaterialTheme.typography.bodyMedium, color = Color.White)
        Spacer(modifier = Modifier.height(16.dp))
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Button(onClick = onPositiveClick, colors = ButtonDefaults.buttonColors(containerColor = Color.Red)) {
                Text(text = when (scenario.title) {
                    " Suspicious Text Message" -> "Click on the text message"
                    "Suspicious Link" -> "Click on the link"
                    else -> "Click on the link"
                }
                )
            }
            Button(onClick = { onNegativeClick
                disableActivityForTwoHours(context)
            }, colors = ButtonDefaults.buttonColors(containerColor = Color.Green)) {
                Text(text = when (scenario.title) {
                    " Suspicious Text Message" -> "Ignore the text message"
                    "Suspicious Link" -> "Ignore the link"
                    else -> "Ignore the link"
                }
                )
            }
        }
    }
}

@Composable
fun FeedbackDialogContent(feedbackMessage: String, onDismiss: () -> Unit) {
    Column(modifier = Modifier
        .padding(16.dp)
        .background(Color.White)) {
        Text(feedbackMessage, style = MaterialTheme.typography.bodyMedium)
        Button(onClick = onDismiss) {
            Text("OK")
        }
    }
}



fun disableActivityForTwoHours(context: Context) {
    val sharedPreferences = context.getSharedPreferences("UnlockDialogActivity", Context.MODE_PRIVATE)
    val editor = sharedPreferences.edit()
    val disableUntil = System.currentTimeMillis() + 60 * 1000// 2 hours in milliseconds
//            2 * 60 * 60 * 1000

    editor.putLong("disableUntil", disableUntil)
    editor.apply()

    // Optionally finish the activity and show a toast
    if (context is Activity) {
        Toast.makeText(context, "You cannot use this activity for 2 hours.", Toast.LENGTH_LONG).show()
        context.finish()
    }
}
@SuppressLint("SuspiciousIndentation")
fun lockDeviceForTwoHours(context: Context) {
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