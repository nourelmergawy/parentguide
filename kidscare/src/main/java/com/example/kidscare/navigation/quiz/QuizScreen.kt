package com.example.kidscare.navigation.quiz

import android.annotation.SuppressLint
import android.content.ContentValues.TAG
import android.content.Context
import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardColors
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.rememberLottieComposition
import com.example.kidscare.Models.KidData
import com.example.kidscare.Models.QuizData
import com.example.kidscare.R
import com.example.kidscare.navigation.Screens

object KidDataRepository {
    private var kidData: KidData? = null

    fun setKidData(data: KidData) {
        kidData = data
    }

    fun getKidData(): KidData? {
        return kidData
    }
}
@Composable
fun QuizScreen(quizViewModel: QuizViewModel,quizId:String,navController: NavController) {
    val kidData: KidData? = KidDataRepository.getKidData()
    Log.d(TAG, "QuizScreen: ")
    val context = LocalContext.current
    val quizData by quizViewModel.quiz.observeAsState(initial = null)
    LaunchedEffect(true) {
        quizViewModel.loadQuiz(quizId)
    }

            quizData?.let { quiz ->
                QuizContent(quiz, kidData!!,context,navController,quizViewModel)
            } ?: run {
                Text("Loading...")
            }
}

@Composable
fun QuizContent(quizData: QuizData?, kidData: KidData, context: Context,navController: NavController,quizViewModel:QuizViewModel) {
    var selectedOption by remember { mutableStateOf<String?>(null) }
    val correctAnswer = quizData?.answer ?: ""
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xffCDFFF0))
        ,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top

    ) {
        item {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {

                TopBar(score = kidData.intialCoins!!, level = 1) // Update these values as needed or make them dynamic
                // Card for displaying the question
                Card(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp), // Add padding around the card
//                        .background(Color(0xFF1BC4B4)),
//            elevation = 4.dp,
                    shape = RoundedCornerShape(16.dp),
                    colors = CardColors(Color(0xFF1BC4B4),Color.Black,Color.Cyan,Color.Cyan)
                ) {
                    Column {
                        quizData?.image?.let {
                            AsyncImage(
                                model = it,
                                contentDescription = "Quiz image",
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(200.dp), // Define a height for the image
                                contentScale = ContentScale.Crop
                            )
                        }

                        Text(
                            text = quizData?.question ?: "",
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Bold,
                            fontSize = 24.sp,
                            modifier = Modifier
                                .wrapContentSize()
                                .padding(8.dp),
                            textAlign = TextAlign.Center, // Center the text within the Text composable
                            lineHeight = 32.sp
                        )
                        Log.d(TAG, "QuizContentanswers: ${quizData?.answers}")
                        Log.d(TAG, "QuizContentData: ${quizData}")

                        quizData?.answers?.values?.forEach { option ->
                            OptionButton(
                                option = option,
                                isSelected = selectedOption == option,
                                correctAnswer = correctAnswer,
                                onSelectOption = { selectedOption = it },
                                navController = navController,
                                quizViewModel = quizViewModel
                            )
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        // Other content such as the 'Next' button
                        // ...
                    }
                }
            }
        }
    }
}

@Composable
fun LevelIndicator(level: Int) {
    // Here, you can draw the level indicator

}

    @Composable
fun TopBar(score: Long, level: Int) {
        Box(
            contentAlignment = Alignment.Center, // This centers all children in the Box
            modifier = Modifier
                .fillMaxWidth() // Takes the full width of the parent
        ) {
            // Draw the icon as the bottom layer
            Image(
                painter = painterResource(id = R.drawable.ic_trophy),
                contentDescription = "Score",
                modifier = Modifier
                    .fillMaxWidth()
                    .size(150.dp), // Define a height for the image
            )

            // Draw the text on top of the icon, centered
            Text(
                text = score.toString(),
                fontWeight = FontWeight.Bold,
                modifier = Modifier.align(Alignment.BottomCenter),
                color = Color.White,// Centers the text in the Box, on top of the icon
                fontSize = 16.sp
            )
        }
}

@Composable
fun OptionButton(option: String, isSelected: Boolean, correctAnswer: String, onSelectOption: (String) -> Unit,navController: NavController,quizViewModel :QuizViewModel) {
    Log.d(TAG, "OptionButton: ${correctAnswer}")
    Log.d(TAG, "OptionButton: ${option}")

    val backgroundColor =when{

        isSelected && option == correctAnswer -> {OpenDialogWithNavigation(navController,true,quizViewModel)
            Color.Green}
        isSelected && option != correctAnswer -> {
            OpenDialogWithNavigation(navController, false,quizViewModel)
            Color.Red
        }
        else -> Color.White
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
fun lotteQuizAnimation(answer :Boolean){

    if (answer){
        val composition by rememberLottieComposition(LottieCompositionSpec.Asset("correct.json"))
            LottieAnimation(
                composition = composition,
                iterations = Int.MAX_VALUE,
                modifier = Modifier.fillMaxWidth(),
                contentScale = ContentScale.FillWidth

            )
    }else{
        val composition by rememberLottieComposition(LottieCompositionSpec.Asset("wrong.json"))
        LottieAnimation(
            composition = composition,
            iterations = Int.MAX_VALUE,
            modifier = Modifier.size(400.dp),
            alignment = Alignment.Center,
            contentScale = ContentScale.FillWidth
        )
    }

}@Composable
fun OpenDialogWithNavigation(navController: NavController, answer: Boolean,quizViewModel :QuizViewModel) {
    var showDialog by remember { mutableStateOf(true) }
    var hasAnswered by remember { mutableStateOf(0) }
    val context = LocalContext.current

    if (!answer) {
        hasAnswered++
    }

    if (showDialog) {
        Dialog(onDismissRequest = { showDialog = false }) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                if (answer) {
                    lotteQuizAnimation(true)
                    Text(
                        text = "Correct",
                        fontSize = 62.sp,
                        modifier = Modifier.padding(16.dp),
                        color = Color.Green
                    )
                    Button(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        onClick = {
                            showDialog = false
                            hasAnswered = 0  // Reset counter on correct answer
                            navController.navigate(Screens.KidHome.screen)
                        }
                    ) {
                        Text(
                            "Go to KidHome",
                            fontSize = 24.sp,
                            modifier = Modifier.padding(16.dp)
                        )
                    }
                } else {
                    if (hasAnswered > 2) {
                        Text(
                            text = "You have reached the maximum number of tries. The phone will be locked for 30m.",
                            fontSize = 32.sp,
                            color = Color.White,
                            modifier = Modifier.padding(16.dp)
                        )
                        // Perform your locking action here
                        // Consider resetting hasAnswered if appropriate
                    } else {
                        lotteQuizAnimation(false)
                        Text(
                            text = "Incorrect",
                            fontSize = 32.sp,
                            color = Color.Red
                        )
                        quizViewModel.handleWrongAnswer(context)

                        Button(
                            onClick = {
                                showDialog = false
                            }
                        ) {
                            Text(
                                "Try Again",
                                fontSize = 24.sp,
                                modifier = Modifier.padding(16.dp)
                            )
                        }
                    }
                }
            }
        }
    }

}
