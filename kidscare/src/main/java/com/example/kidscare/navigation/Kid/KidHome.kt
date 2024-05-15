package com.example.kidscare.navigation.Kid

import android.annotation.SuppressLint
import android.content.ContentValues
import android.content.ContentValues.TAG
import android.content.Context
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
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
import androidx.lifecycle.LiveData
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.kidscare.KidDataRepository
import com.example.kidscare.Models.KidData
import com.example.kidscare.Models.QuizData
import com.example.kidscare.R
import com.example.kidscare.navigation.Screens
import com.example.kidscare.navigation.quiz.QuizViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlin.math.log

@Composable
fun homeKidScreen(quizViewModel : QuizViewModel, navController : NavController){
    val quizData by quizViewModel._quizzes.observeAsState(initial = null)
    val context = LocalContext.current
    val kidData: KidData? = KidDataRepository.getKidData()
    val coins by quizViewModel.coins.observeAsState(initial = null)

    LaunchedEffect(true) {
        quizViewModel.loadAllQuiz()
        quizViewModel.getCoins(kidData?.uid!!,context)
    }
    LazyColumn (modifier = Modifier
        .background(Color(0xffCDFFF0))
        .fillMaxSize()){
        item {
            coins?.let {
                CoinsCard(item = it)
            }?: run {
                Text("Loading...")
            }
    quizData?.let { quiz ->
        Log.d(ContentValues.TAG, "quizData: ${quizData}")
                HorizontalLazyColumn(quizzes = quiz.subList(3,12),quizViewModel,navController)
                ScenariosHorizontalLazyColumn(quiz.subList(0,3),quizViewModel,navController)
            }?: run {
            Text("Loading...")
        }
        appPermissions(navController)
        StoreCard(kidId = KidDataRepository.getKidData()!!.uid.toString()!!, context = context, quizViewModel = quizViewModel)
    }
    }

}

@Composable
fun CoinsCard(item: Long) {

    Text(
        text = "Coins",
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
                    painter = painterResource(id = R.drawable.moneyjar),
                    contentDescription = "Coins",
                    modifier = Modifier
                        .size(200.dp)
                        .weight(1f)
                )
                Column(
                    modifier = Modifier
                        .padding(start = 8.dp)
                        .weight(1f), horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Coins",
                        fontSize = 50.sp,
                        color = Color.Black,
                        fontWeight = FontWeight.Bold
                    )
                    Row {
//                        Image(
//                            painter = painterResource(id = R.drawable.img_coin),
//                            contentDescription = null,
//                            Modifier.size(75.dp)
//                        )
                        Text(
                            text = item.toString(),
                            fontSize = 50.sp,
                            color = Color.Black,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(
                                top = 8.dp
                            )
                        )
                    }
                }
            }
        }
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
                                .background(Color(0xFFBBF1E7)),
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


@SuppressLint("SuspiciousIndentation")
@Composable
fun ScenariosHorizontalLazyColumn(
    quizzes: List<QuizData>?,
    quizViewModel: QuizViewModel,
    navController: NavController
) {
    val context = LocalContext.current
//    Log.d(ContentValues.TAG, "HorizontalLazyColumn: ${quizzes}")

    Column (modifier = Modifier
        .background(Color(0xFFBBF1E7))
        .fillMaxSize()){

        Text(text = "Ireal life scenarios ",
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
                        .background(Color(0xFFBBF1E7)),
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
fun StoreCard(context: Context,kidId: String,quizViewModel:QuizViewModel) {
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
                var showDialog by remember { mutableStateOf(false) }

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

                            onClick = {
                                showDialog = true},
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF003D31)),
                            contentPadding = PaddingValues(horizontal = 32.dp)
                        ) {
                            Text(
                                text = "Buy",
                                fontSize = 18.sp
                            )
                        }

                        // Dialog that will show when the button is clicked
                        if (showDialog) {

                            AlertDialog(
                                onDismissRequest = { showDialog = false },
                                title = {
                                    Text(text =
                                    if (checkPrice(
                                            coins = quizViewModel.coins,
                                            price = 300, quizViewModel = quizViewModel, kidId = kidId, context = context)){
                                    "Purchase Successful"
                                }else{ "Purchase faild"}
                                        , style = MaterialTheme.typography.bodyMedium) },
                                text = { Text(text =
                                if (checkPrice(
                                        coins = quizViewModel.coins,
                                        price = 300, quizViewModel = quizViewModel, kidId = kidId, context = context)){
                                    "This item has been successfully bought."
                                }else{ "You don't have enough coins to buy this item."}
                                ) },
                                confirmButton = {
                                    Button(onClick = { showDialog = false }) {
                                        Text("OK")
                                    }
                                }
                            )
                        }
                    }

                }
            }
        }
    }
fun checkPrice(
    coins: LiveData<Long?>,
    price: Long,
    quizViewModel: QuizViewModel,
    kidId: String,
    context: Context
): Boolean {
    coins.value?.let { currentCoins ->
        Log.d(TAG, "checkPrice: $currentCoins")
        if (currentCoins >= price) {
            quizViewModel.addKidCoins(-price.toInt(), kidId, context)  // Deducting the price from the coins
            return true
        }
    }
    return false
}
