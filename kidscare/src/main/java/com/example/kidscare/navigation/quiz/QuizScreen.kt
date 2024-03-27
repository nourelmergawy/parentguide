package com.example.kidscare.navigation.quiz

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.kidscare.navigation.Model.QuizData

@Composable
fun quiz() {
    LazyColumn {
        items(1000) {
            Box(
                modifier = Modifier
                    .background(Color(0xFF073A3D)) // Corrected the hex code to include the alpha value as FF for full opacity
                    .padding(8.dp)
            ) {
                Text(text = "quiz Text", fontSize = 24.sp)
            }
        }
    }
}
@Composable
fun QuizScreen(viewModel: QuizViewModel) {
    // Observe the quiz LiveData from the ViewModel
    val quizData by viewModel.quiz.observeAsState(initial = null)

    // Load the quiz data
    LaunchedEffect(true) {
        viewModel.loadQuiz("1")
    }

    // Ensure QuizContent can handle nullable quizData
    if (quizData != null) {
        QuizContent(quizData)
    } else {
        // Show a loading indicator or some placeholder while quizData is null
        Text("Loading...")
    }
}
@Composable
fun QuizContent(quizData: QuizData?, modifier: Modifier = Modifier) {
    // Initialize state for selected option
    var selectedOption by remember { mutableStateOf("") }

    // Ensure quizData is not null before building the UI
    quizData?.let { quiz ->
        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = quiz.name ?: "",
                fontWeight = FontWeight.Bold,
                style = MaterialTheme.typography.titleMedium
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(quiz.question ?: "")
            Spacer(modifier = Modifier.height(16.dp))

            // Assuming `answers` is a map and you need to display its values
            quiz.answers?.values?.let { answers ->
                OptionsGroup(
                    options = answers.toList(), // Convert the collection to List
                    selectedOption = selectedOption,
                    onOptionSelected = { selectedOption = it }
                )
            }

            Spacer(modifier = Modifier.height(16.dp))
            Button(
                onClick = { /* handle send click */ },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Send")
            }
        }
    } ?: run {
        // Handle the case where quizData is null (e.g., loading state)
        Text("Loading...", modifier = modifier.padding(16.dp))
    }
}
@Composable
fun OptionsGroup(options: List<String>, selectedOption: String, onOptionSelected: (String) -> Unit) {
    Column {
        options.forEach { option ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                RadioButton(
                    selected = selectedOption == option,
                    onClick = { onOptionSelected(option) }
                )
                Text(text = option, modifier = Modifier.padding(start = 8.dp))
            }
        }
    }
}

