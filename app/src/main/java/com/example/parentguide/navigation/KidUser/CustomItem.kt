package com.example.parentguide.navigation.KidUser

import android.content.ContentValues.TAG
import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImagePainter
import coil.compose.rememberImagePainter
import com.example.parentguide.Models.KidData
import com.example.parentguide.R

@Composable
fun CustomItem(viewModel: HomeViewModel){
    val state by viewModel.kidDataStateFlow.collectAsState()

    when (state) {
        is DataState.Success -> {
            val data = (state as DataState.Success<List<KidData>>).data
            // Display the data
            ShowLazyList(data)
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
    fun ShowLazyList(kidDatas: List<KidData>) {
        LazyColumn {
            items(kidDatas) { kidData ->
                Log.d(TAG, "ShowLazyList: ${kidData}")
                CardItem(kidData)
            }
        }
    }

    @Composable
    fun CardItem(kidData: KidData) {
        lateinit var painterGender:AsyncImagePainter
        Card(
            modifier = Modifier
                .width(250.dp)
                .height(250.dp)
                .padding(8.dp)
        ) {

            Box(modifier = Modifier
                .fillMaxSize()
                .background(Color(0xffA5B6D2))) {
                Log.d(TAG, "CardItem: ${kidData.gender}")
                if (kidData.gender == "Male"){
                     painterGender = rememberImagePainter(R.drawable.boy)
                }else{
                     painterGender = rememberImagePainter(R.drawable.girl)
                }
                Image(
                    painter = painterGender,
                    modifier = Modifier
                        .width(200.dp)
                        .height(200.dp)
                        .align(Alignment.Center),
                    contentDescription = "My content description",
                    contentScale = ContentScale.FillWidth,
                )

                Text(
                    text = kidData.username!!,
                    fontSize = 32.sp,
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .fillMaxWidth()
                        .background(Color.White)
                        ,
                    textAlign = TextAlign.Center,
                    color = Color.Black
                )

            }

        }
    }