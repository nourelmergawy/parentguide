package com.example.kidscare

import android.annotation.SuppressLint
import android.app.ActivityManager
import android.content.ContentValues.TAG
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.setContent
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.rememberLottieComposition
import com.example.kidscare.Models.QuizData
import com.example.kidscare.Models.QuizScore
import com.example.kidscare.navigation.quiz.OpenDialogWithNavigation
import com.example.kidscare.navigation.quiz.QuizViewModel
import com.example.kidscare.service.UnlockService
import kotlin.random.Random

class UnlockDialogActivity : AppCompatActivity() {
    private lateinit var quizViewModel: QuizViewModel

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        quizViewModel = ViewModelProvider(this).get(QuizViewModel::class.java)


        setContent {
            val serviceIntent = Intent(this, UnlockService::class.java)
            startForegroundService(serviceIntent)
            val isMyServiceRunning = isServiceRunning(this, UnlockService::class.java)
            Toast.makeText(this, "Service running: $isMyServiceRunning", Toast.LENGTH_SHORT).show()
            // Shuffle the list and take the first three elements
//            val shuffledList = quizViewModel.quizzes.value!!.shuffled().take(3)
            val quizData by quizViewModel._quizzes.observeAsState(initial = null)

            LaunchedEffect(true) {
                quizViewModel.loadAllQuiz()
            }
            quizData?.let { quiz ->
                val randomNumber = Random.nextInt(1, quiz.size)

                Column (modifier = Modifier
                    .fillMaxSize()
                    )
                {
//                    Row {
                        Log.d(TAG, "onCreate: ${quizData!!::class}")
                        for (item in 0..2){
                            AsyncImage(
                                model = R.drawable.unknown,
                                contentDescription = "dialog image",
                                modifier = Modifier
                                    .fillMaxHeight()
                                    .fillMaxWidth()

                            )
                        }
//                    }


                        Log.d(TAG, "onCreate: ${quizData!!::class}")
                        var showDialog by remember { mutableStateOf(true) }

                        BottomDialog(
                            showDialog = showDialog,
                            onDismissRequest = { showDialog = false },
                            onPositiveButtonClick = { /* Handle positive button click */ },
                            onNegativeButtonClick = { /* Handle negative button click */ },
                        )
                    }



            } ?: run {
                Text("Loading...")
            }

//            DialogContent(onDismiss = { finish() })

        }
    }


    @Composable
    fun BottomDialog(
        showDialog: Boolean,
        onDismissRequest: () -> Unit,
        onPositiveButtonClick: () -> Unit,
        onNegativeButtonClick: () -> Unit,
    ) {
        if (showDialog) {
            Dialog(onDismissRequest = onDismissRequest) {
                Surface(
                    modifier = Modifier.fillMaxWidth()
,
                    shape = RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp),
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(Color.White)
                            .padding(16.dp)
                    ) {
                        var answers = listOf(
                            "A) Answer the call immediately",
                            "B) Ignore the call and not tell anyone",
                            "C) Ignore the call and inform a parent or guardian",
                        )
                        var selectedOption by remember { mutableStateOf<String?>(null) }

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {

                            Text(
                                text = "Scenario : Receiving an unknown call",
                                style = MaterialTheme.typography.bodyMedium,
                                color = Color.Black
                            )

                            }
                        Divider(color = Color.LightGray, thickness = 1.dp)
                        Spacer(modifier = Modifier.height(8.dp))

                        Text(
                            text = "So What should the child do?",
                            style = MaterialTheme.typography.bodyMedium,
                            color = Color.Black
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        answers.forEach { answer ->
                            OptionButton(
                                option = answer,
                                isSelected = selectedOption == answer,
                                correctAnswer = "C) Ignore the call and inform a parent or guardian",
                                onSelectOption = { selectedOption = it }
                            )}

                        Row( modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                            horizontalArrangement = Arrangement.Center){
                            Button(onClick = { /*TODO*/ },
                                ) {

                                Text(
                                    text = "Next",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = Color.Black
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun OptionButton(option: String, isSelected: Boolean, correctAnswer: String, onSelectOption: (String) -> Unit) {
    Log.d(TAG, "OptionButton: ${correctAnswer}")
    Log.d(TAG, "OptionButton: ${option}")

    val backgroundColor =when{

        isSelected && option == correctAnswer -> {
            lotteQuizAnimation(true)
            Color.Green}
        isSelected && option != correctAnswer -> {
            lotteQuizAnimation(false)

            Color.Red
        }
        else -> Color.Cyan
    }
    Button(
        onClick = { onSelectOption(option) },
        colors = ButtonDefaults.buttonColors(containerColor = backgroundColor),
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
    ) {
        Text(text = option, modifier = Modifier.padding(16.dp), color = Color.Black)
    }
}

    @SuppressLint("SuspiciousIndentation")
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