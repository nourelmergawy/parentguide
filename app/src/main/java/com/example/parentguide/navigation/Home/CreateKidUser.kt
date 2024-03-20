package com.example.parentguide.navigation.Home

import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.parentguide.Models.KidData
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

@Composable
fun CreateKidUser(
    navController: NavHostController,
    state: HomeState,
    OnCreateClick: () -> Unit,
) {
    val context = LocalContext.current
    { TODO("maxmuim 4 kids") }
    Column(
        modifier = Modifier.padding(20.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        val username = remember {
            mutableStateOf(TextFieldValue())
        }

        val password = remember {
            mutableStateOf(TextFieldValue())
        }

        val passwordConfirm = remember {
            mutableStateOf(TextFieldValue())
        }
        val age = remember {
            mutableStateOf(TextFieldValue())
        }

        val dailyLoginHours = remember {
            mutableStateOf(TextFieldValue())
        }

        val intialCoins = remember {
            mutableStateOf(TextFieldValue())
        }

        Text(
            text = "Create Kid User",
            style = TextStyle(fontSize = 40.sp, fontFamily = FontFamily.Cursive)
        )

        Spacer(modifier = Modifier.height(15.dp))

        TextField(
            label = { Text(text = "Username") },
            value = username.value,
            onValueChange = { username.value = it }
        )

        Spacer(modifier = Modifier.height(15.dp))

        TextField(
            label = { Text(text = "Password") },
            value = password.value,
            onValueChange = { password.value = it },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password)
        )

        Spacer(modifier = Modifier.height(15.dp))

        TextField(
            label = { Text(text = "Password Confirmation") },
            value = passwordConfirm.value,
            onValueChange = { passwordConfirm.value = it },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password)
        )

        Spacer(modifier = Modifier.height(15.dp))

        TextField(
            label = { Text(text = "Age") },
            value = age.value,
            onValueChange = { age.value = it }
        )

        Spacer(modifier = Modifier.height(15.dp))

        TextField(
            label = { Text(text = "Daily Hours Usage") },
            value = dailyLoginHours.value,
            onValueChange = { dailyLoginHours.value = it }
        )

        Spacer(modifier = Modifier.height(15.dp))

        TextField(
            label = { Text(text = "Intial Coins") },
            value = intialCoins.value,
            onValueChange = { intialCoins.value = it }
        )

        Spacer(modifier = Modifier.height(15.dp))


        val kidUser = KidData(
            username.value.text,
            password.value.text,
            age.value.text,
            dailyLoginHours.value.text,
            intialCoins.value.text
        )

        Box(modifier = Modifier.padding(40.dp, 0.dp, 40.dp, 0.dp)) {
            Button(
                onClick = {


                    val databaseRefrance = FirebaseDatabase.getInstance().getReference("Kids Users")
                    databaseRefrance.addListenerForSingleValueEvent(object : ValueEventListener {
                        override fun onDataChange(dataSnapshot: DataSnapshot) {
                            val count = dataSnapshot.childrenCount
                            databaseRefrance.child("Kid ${count+1} }").setValue(kidUser).addOnSuccessListener {
                                Toast.makeText(context
                                    ,
                                    "Created kid user",
                                    Toast.LENGTH_LONG
                                ).show()
                                navController.navigate("Home")

                            }.addOnFailureListener {
                                Toast.makeText(context
                                    ,
                                    "faild",
                                    Toast.LENGTH_LONG
                                ).show()
                            }

                        }

                        override fun onCancelled(databaseError: DatabaseError) {
                            // Handle error
                        }
                    })

                    },
                shape = RoundedCornerShape(50.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
            ) {
                Text(text = "Create")
            }
        }
    }
}
