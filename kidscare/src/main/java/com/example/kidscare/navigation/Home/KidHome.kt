package com.example.kidscare.navigation.Home

import android.content.ContentValues
import android.util.Log
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.kidscare.Models.QuizData
import com.example.kidscare.R
import com.example.kidscare.navigation.Screens
import com.example.kidscare.navigation.quiz.QuizViewModel

@Composable
fun homeKidScreen(quizViewModel : QuizViewModel,navController : NavController){
    val quizData by quizViewModel._quizzes.observeAsState(initial = null)

    LaunchedEffect(true) {
        quizViewModel.loadAllQuiz()
    }
    quizData?.let { quiz ->
        Log.d(ContentValues.TAG, "quizData: ${quizData}")
        LazyColumn {
            item {
                HorizontalLazyColumn(quiz)
                appPermissions(navController)

            }}
    } ?: run {
        Text("Loading...")
    }


}
@Composable
fun HorizontalLazyColumn(quizzes: List<QuizData>?) {
    Log.d(ContentValues.TAG, "HorizontalLazyColumn: ${quizzes}")

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

                            // For more complex coloring, consider using Card's contentColor and other properties
                        ) {
                            Column(modifier = Modifier
                                .padding(8.dp)
                                .background(Color(0xffAEFFEB))) {

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
    Column (modifier = Modifier.fillMaxWidth()
        .clickable {
            navController.navigate(Screens.PermissionScreen.screen)
    },
        horizontalAlignment = Alignment.CenterHorizontally,
//        verticalArrangement = Arrangement.SpaceEvenly

       ){
        Column {
            Text(text = "Quiz",
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
