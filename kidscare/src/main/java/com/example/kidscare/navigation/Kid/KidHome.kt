package com.example.kidscare.navigation.Kid

import android.annotation.SuppressLint
import android.content.ContentValues
import android.content.ContentValues.TAG
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion.CenterHorizontally
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
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
                StoreCard()
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
                .background(Color(0xFFBBF1E7))
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
                    .background(Color(0xFFBBF1E7))
                )
                {
                    items(quizzes!!.size) {item ->

                        Card(
                            modifier = Modifier
                                .width(300.dp)
                                .height(300.dp)
                                .padding(8.dp)
                                .background(Color(0xFFBBF1E7 )),
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
                            Column (modifier = Modifier
                                .fillMaxSize()
                                .background(Color(0xffAEFFEB))
                                .padding(16.dp)){

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
                                Text(
                                    text = quizzes!!.get(item).question,
                                    textAlign = TextAlign.Center,
                                    color = Color.Black,
                                    fontSize = 24.sp,
                                    modifier = Modifier.padding(8.dp)
                                )
                            }

                        }
                    }
                }
            }
        }

@Composable
fun appPermissions (navController:NavController){
    Column {
        Text(
            text = "Permissions",
            fontSize = 32.sp,
            color = Color(0xff1B2B48),
            textAlign = TextAlign.Left,
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
        )

        val context = LocalContext.current
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color(0xffAEFFEB))
                .padding(16.dp)
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

        ) {
            Card(
                shape = RoundedCornerShape(45.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp)
            ) {

                Column(
                    modifier = Modifier
                        .background(Color(0xFFBBF1E7))
                        .padding(8.dp)
                        .fillMaxWidth()
                ) {
                    AsyncImage(
                        model = R.drawable.monitor,
                        contentDescription = "App permissions ",
                        modifier = Modifier
                            .width(600.dp)
                            .height(200.dp)
                            .padding(16.dp), // Define a height for the image
                        contentScale = ContentScale.Crop
                    )
                    Text(
                        text = "Block Apps",
                        fontSize = 24.sp,
                        color = Color.Black,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth()
                    )

                }

            }
        }
    }
}



@Composable
fun StoreCard() {
    Text(
        text = "Store",
        fontSize = 31.sp,
        modifier = Modifier.padding(8.dp),
        color = Color(0xFF1B2B48)
    )

        Column(
            modifier = Modifier
                .background(Color(0xffAEFFEB))
                .padding(8.dp)
                .fillMaxWidth()
        ) {

            Card(
                shape = RoundedCornerShape(43.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .background(Color(0xFFBBF1E7))
                        .padding(8.dp)
                        .fillMaxWidth()
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.img_store),
                        contentDescription = "Cody Avatar",
                        modifier = Modifier
                            .size(100.dp)
                            .weight(1f)
                    )
                    Column(
                        modifier = Modifier
                            .padding(start = 8.dp)
                            .weight(1f), horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "Cody",
                            fontSize = 24.sp,
                            color = Color.Black,
                            fontWeight = FontWeight.Bold
                        )
                        Row {
                            Image(
                                painter = painterResource(id = R.drawable.img_coin),
                                contentDescription = null,
                                Modifier.size(38.dp)
                            )
                            Text(
                                text = "$300",
                                fontSize = 18.sp,
                                color = Color.Black,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(
                                    top = 8.dp
                                )
                            )
                        }

                        Spacer(modifier = Modifier.height(4.dp))
                        Button(
                            onClick = { /* TODO: Handle buy action */ },
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF003D31)),
                            contentPadding = PaddingValues(horizontal = 32.dp)
                        ) {
                            Text(
                                text = "Buy",
                                fontSize = 18.sp
                            )
                        }
                    }

                }
            }


        }
    }
