package com.example.kidscare.navigation.Home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.navigation.NavController
import com.example.kidscare.navigation.Kid.CustomItem

@Composable
fun Home (
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
    }
}
