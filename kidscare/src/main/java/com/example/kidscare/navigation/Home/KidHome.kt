package com.example.kidscare.navigation.Home

import android.content.ContentValues
import android.util.Log
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.kidscare.Models.QuizData
import com.example.kidscare.navigation.quiz.QuizViewModel

@Composable
fun homeKidScreen(quizViewModel : QuizViewModel){
    val quizData by quizViewModel._quizzes.observeAsState(initial = null)

    LaunchedEffect(true) {
        quizViewModel.loadAllQuiz()
    }
    quizData?.let { quiz ->
        Log.d(ContentValues.TAG, "quizData: ${quizData}")
        HorizontalLazyColumn(quiz)
    } ?: run {
        Text("Loading...")
    }
}
@Composable
fun HorizontalLazyColumn(quizzes: List<QuizData>?) {
    Log.d(ContentValues.TAG, "HorizontalLazyColumn: ${quizzes}")
    LazyRow(modifier = Modifier.padding(horizontal = 16.dp)) {
        items(quizzes!!.size) {item ->
            Card(
                modifier = Modifier
                    .width(300.dp)
                    .height(250.dp)
                    .padding(8.dp), // Add padding around the card
                shape = RoundedCornerShape(16.dp),
                // For more complex coloring, consider using Card's contentColor and other properties
            ) {
                Column(modifier = Modifier.padding(8.dp)) {

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
                        textAlign = TextAlign.End,
                        color = Color.White,

                           )
                    // Add more components here if needed, they will be arranged vertically
                }
            }
        }
    }
}