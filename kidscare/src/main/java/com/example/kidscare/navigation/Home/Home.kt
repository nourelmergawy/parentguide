package com.example.kidscare.navigation.Home

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardColors
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.kidscare.R
import com.example.kidscare.navigation.Kid.CustomItem

@Composable
fun Home(
    viewModel: HomeViewModel,
    navController: NavController, // Add this parameter
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(color = Color(0xFFCDFFF0)),

        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {


        CustomItem(
            viewModel = viewModel,
            navController = navController // Pass the navController here,

        )
        StoreCard()
    }
}

@Composable
fun StoreCard() {
    Card(
        shape = RoundedCornerShape(38.dp),
        // add argb color
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
    ) {
        Column(
            modifier = Modifier
                .background(Color(0xFF99E3D5))
                .padding(8.dp)
                .fillMaxWidth()
        ) {
            Text(
                text = "Store",
                fontSize = 31.sp,
                modifier = Modifier.padding(8.dp),
                color = Color(0xFF1B2B48)
            )
            Card(
                shape = RoundedCornerShape(43.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .background(Color(0xFF6BA096))
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
}
