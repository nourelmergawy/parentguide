package com.example.parentguide.navigation.Home

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun Home (
    state: HomeState,
    OncreateKidUserClick: () -> Unit,
    viewModel : HomeViewModel
) {

    val context = LocalContext.current

    LaunchedEffect(key1 = state.CreateKidUserError) {
        state.CreateKidUserError?.let { error ->
            Toast.makeText(
                context,
                error,
                Toast.LENGTH_LONG
            ).show()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(color = Color(0xFFBACAE7)),

        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Button(
            onClick = { OncreateKidUserClick() },
            modifier = Modifier
                .padding(8.dp)
                .fillMaxWidth()
                .wrapContentHeight()
            , colors = ButtonDefaults.buttonColors(Color(0xFF212A3E))

        ) {

            Text(text = "Create kid user", color = Color.White , fontSize = 24.sp, modifier = Modifier.padding(8.dp))
        }

        CustomItem(viewModel = viewModel)
    }


}