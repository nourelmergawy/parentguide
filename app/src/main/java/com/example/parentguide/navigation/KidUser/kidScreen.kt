package com.example.parentguide.navigation.KidUser

import android.content.ContentValues
import android.content.ContentValues.TAG
import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import com.example.parentguide.Models.KidData
import com.example.parentguide.R
import com.example.parentguide.navigation.Screens

@Composable
fun displayKid(homeViewModel: HomeViewModel,kidId : String?, navController: NavHostController){
    val state by homeViewModel.singleKidDataStateFlow.collectAsState()

    LaunchedEffect(true) {
        Log.d(TAG, "displayKid: $kidId")
        homeViewModel.fetechKidDate(kidId!!)
    }
    when (state) {
        is DataState.Success -> {
            Log.d(ContentValues.TAG, "CustomItem: ${(state as DataState.Success<KidData>).data}")
            val data = (state as DataState.Success<KidData>).data
//            Log.d(TAG, "displayKid: $data")
            // Display the data
            kidItem(data,homeViewModel,navController)
        }
        is DataState.Failure -> {
            val message = (state as DataState.Failure).message

            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = message,
                    fontSize = 24.sp,
                )
            }

        }

        DataState.Loading -> {
            Box(
                modifier = Modifier,
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        }
        DataState.Empty -> {
            // Show empty state or initial UI
        }

        else -> {
            Box(
                modifier = Modifier,
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Error Fetching data",
                    fontSize = 24.sp,
                )
            }
        }
    }
}
@Composable
fun kidItem(kidData: KidData,homeViewModel: HomeViewModel, navController: NavHostController){
    val coins = remember { mutableStateOf(TextFieldValue()) }
    val context = LocalContext.current
    Column(
        modifier = Modifier.fillMaxSize().background(color = Color(0xFFBACAE7)),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        if(kidData.gender != null) {
            AsyncImage(
                model =when{
                    kidData.gender == "Male" -> R.drawable.boy
                    kidData.gender == "Female" -> R.drawable.girl
                    else -> R.drawable.boy
                },
                contentDescription = "Profile picture",
                modifier = Modifier
                    .size(150.dp)
                    .clip(CircleShape),
                contentScale = ContentScale.Crop
            )
            Spacer(modifier = Modifier.height(16.dp))
        }
        if(kidData.username != null) {
            Text(
                text = kidData.username,
                textAlign = TextAlign.Center,
                fontSize = 36.sp,
                fontWeight = FontWeight.SemiBold
            )
        }
        Button(colors = ButtonDefaults.buttonColors(containerColor = Color.Red),
            onClick = {
                homeViewModel.deleteItemFromFirebase(kidData.uid.toString()!!,context)
                navController.navigate(Screens.Home.screen)
        }) {
            Text(text = "remove Kid")
        }
        OutlinedTextField(
            label = { Text(text = "Intial Coins") },
            value = coins.value,
            onValueChange = { coins.value = it },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
        )
        Button(colors = ButtonDefaults.buttonColors(containerColor = Color.Green),
            onClick = {
                homeViewModel.addKidCoins(coins.value.text.toInt(), kidData.uid.toString()!!,context)
            }) {
            Text(text = "Add")
        }
    }
}