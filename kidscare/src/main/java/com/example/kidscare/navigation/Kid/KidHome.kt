package com.example.kidscare.navigation.Kid

import android.annotation.SuppressLint
import android.content.ContentValues
import android.content.ContentValues.TAG
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.kidscare.Models.QuizData
import com.example.kidscare.R
import com.example.kidscare.navigation.Screens
import com.example.kidscare.navigation.quiz.QuizViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

@Composable
fun homeKidScreen(quizViewModel : QuizViewModel,navController : NavController){
    val quizData by quizViewModel._quizzes.observeAsState(initial = null)

    LaunchedEffect(true) {
        quizViewModel.loadAllQuiz()
    }
    quizData?.let { quiz ->
        Log.d(ContentValues.TAG, "quizData: ${quizData}")
        LazyColumn (modifier = Modifier
            .background(Color(0xffCDFFF0))
            .fillMaxSize()){
            item {
                HorizontalLazyColumn(quiz,quizViewModel,navController)
                appPermissions(navController)

            }}
    } ?: run {
        Text("Loading...")
    }


}
@SuppressLint("SuspiciousIndentation")
@Composable
fun HorizontalLazyColumn(
    quizzes: List<QuizData>?,
    quizViewModel: QuizViewModel,
    navController: NavController
) {
    val context = LocalContext.current
//    Log.d(ContentValues.TAG, "HorizontalLazyColumn: ${quizzes}")

            Column (modifier = Modifier
                .background(Color(0xffCDFFF0))
                .fillMaxSize()){

                Text(text = "Quiz",
                    fontSize =32.sp,
                    color = Color(0xff1B2B48),
                    textAlign = TextAlign.Left,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp))

                LazyRow(modifier = Modifier
                    .padding(horizontal = 16.dp)
                    .background((Color(0xffAEFFEB))),) {
                    items(quizzes!!.size) {item ->

                        Card(
                            modifier = Modifier
                                .width(300.dp)
                                .height(300.dp)
                                .padding(8.dp)
                                .background(Color(0xffAEFFEB))
                                ,
                            shape = RoundedCornerShape(16.dp),
                            onClick = {
                                Log.d(TAG, "HorizontalLazyColumn: ${item}")

                                    GlobalScope.launch(Dispatchers.Main) {
                                        Log.d(TAG, "HorizontalLazyColumn: ${quizViewModel.isQuizSolved(item.toString())}")

                                        when (quizViewModel.isQuizSolved(item.toString())) {
                                            "solved" -> {
                                                Toast.makeText(
                                                    context,
                                                    "You have already solved this quiz",
                                                    Toast.LENGTH_LONG
                                                ).show()
                                            }
                                            "wrongAnswer", "notSolved" -> {
                                                navController.navigate("kidquiz/${item}")
                                            }
                                        }
                                    }
                            }

                            // For more complex coloring, consider using Card's contentColor and other properties
                        ) {
                            Column(modifier = Modifier
                                .padding(8.dp)
                                .background(Color(0xffAEFFEB))

                                ) {

                                quizzes.get(item).image?.let {
                                    AsyncImage(
                                        model = it,
                                        contentDescription = "Quiz image",
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .height(200.dp), // Define a height for the image
                                        contentScale = ContentScale.Crop
                                    )
                                }
                                Text(text = quizzes!!.get(item).question,
                                    textAlign = TextAlign.Center,
                                    color = Color.White,
                                    fontSize = 24.sp,
                                    modifier = Modifier.padding(8.dp)
                                    )
                                // Add more components here if needed, they will be arranged vertically
                            }
                        }
                    }
                }
            }
        }

@Composable
fun appPermissions (navController:NavController){
    val context = LocalContext.current
    Column (modifier = Modifier
        .fillMaxWidth()
        .clickable {
            if (android.os.Build.VERSION.SDK_INT < 28) {
                navController.navigate(Screens.PermissionScreen.screen)
            } else {
                Toast
                    .makeText(
                        context,
                        "this feature is not supported for your device",
                        Toast.LENGTH_LONG
                    )
                    .show()
            }

        },
        horizontalAlignment = Alignment.CenterHorizontally,
//        verticalArrangement = Arrangement.SpaceEvenly

       ){
        Column {
            Text(text = "Permissions",
                fontSize =32.sp,
                color = Color(0xff1B2B48),
                textAlign = TextAlign.Left,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp))

            AsyncImage(
                model = R.drawable.monitor,
                contentDescription = "App permissions ",
                modifier = Modifier
                    .width(600.dp)
                    .height(200.dp)
                    .padding(horizontal = 16.dp), // Define a height for the image
                contentScale = ContentScale.Crop
            )
        }
    }
}
