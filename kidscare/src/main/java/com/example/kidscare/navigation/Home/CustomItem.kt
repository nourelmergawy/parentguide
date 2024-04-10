package com.example.kidscare.navigation.Home

import android.content.ContentValues.TAG
import android.util.Log
import android.widget.Toast
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
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.AsyncImagePainter
import coil.compose.rememberImagePainter
import com.example.kidscare.Models.KidData
import com.example.kidscare.R
import com.example.kidscare.navigation.Screens
import java.security.MessageDigest

@Composable
fun CustomItem(viewModel: HomeViewModel, navController: NavController) {
    val state by viewModel.kidDataStateFlow.collectAsState()
    when (state) {
        is DataState.Success -> {
            Log.d(TAG, "CustomItem: ${(state as DataState.Success<List<KidData>>).data}")
            val data = (state as DataState.Success<List<KidData>>).data
            // Display the data
            ShowLazyList(data, navController = navController,viewModel)
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
    fun ShowLazyList(kidDatas: List<KidData>, navController: NavController,viewModel: HomeViewModel) {
        Log.d(TAG, "kidDatas: $kidDatas")
        LazyColumn {
            items(kidDatas) { kidData ->
                Log.d(TAG, "ShowLazyList: $kidData")
                
                CardItem(kidData, navController,viewModel)
            }
        }
    }

    @Composable
    fun CardItem(kidData: KidData, navController: NavController,viewModel: HomeViewModel) {
        lateinit var painterGender:AsyncImagePainter
        // This state controls whether the dialog is shown or not
        Log.d(TAG, "CardItem: $kidData")
        var showDialog by remember { mutableStateOf(false) }
        Card(
            modifier = Modifier
                .width(250.dp)
                .height(250.dp)
                .padding(8.dp),
            onClick = { showDialog = true}
        ) {
            if (showDialog) {
                PasswordInputDialog(
                    onDismiss = { showDialog = false },
                    kidData = kidData,
                    navController = navController,
                    viewModel = viewModel
                )
            }
            Box(modifier = Modifier
                .fillMaxSize()
                .background(Color(0xffA5B6D2)),
                ) {
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
@Composable
fun PasswordInputDialog(
    onDismiss: () -> Unit,
    kidData: KidData,
    navController: NavController,
    viewModel: HomeViewModel
) {
    val context = LocalContext.current

    var password by remember { mutableStateOf("") }
    var isPasswordValid by remember { mutableStateOf(false) }

    if (isPasswordValid) {
        onDismiss()
    } else {
        AlertDialog(
            onDismissRequest = { onDismiss() },
            title = { Text("Enter Password") },
            text = {
                OutlinedTextField(
                    value = password,
                    onValueChange = { password = it },
                    label = { Text("Password") },
                    keyboardOptions = KeyboardOptions.Default.copy(autoCorrect = false)
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        val hashedPassword = hashPassword(password)

                        isPasswordValid = comparePasswored(hashedPassword, kidData.password)
                        if (isPasswordValid) {
                            Toast.makeText(context, "correct", Toast.LENGTH_LONG).show()
                            Log.d(TAG, "PasswordInputDialog: $kidData")
                            viewModel.fetchKidData(kidData)
                            navController.navigate(Screens.KidHome.screen) // Navigate to QuizScreen
                        }
                        if (!isPasswordValid) {
                            // Handle wrong password case
                            onDismiss() // Consider whether to dismiss or show an error message
                            Toast.makeText(context, "wrong", Toast.LENGTH_LONG).show()

                        }
                    }
                ) {
                    Text("Done")
                }
            }
        )
    }
}


fun hashPassword(password: String): String {
    val digest = MessageDigest.getInstance("SHA-256")
    val hash = digest.digest(password.toByteArray(Charsets.UTF_8))
    return hash.joinToString("") { "%02x".format(it) }
}

fun comparePasswored (password: String?, kidPassword: String?):Boolean{

    if (password == kidPassword ){
        return true

    }else{
        return false

    }
    return false
}
