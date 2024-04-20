package com.example.kidscare.Notification

import android.content.ContentValues
import android.content.ContentValues.TAG
import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.AbsoluteAlignment
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.kidscare.KidDataRepository
import com.example.kidscare.Models.KidNotifications
import com.example.kidscare.R

@Composable
fun Notification( notificationsViewModel: NotificationsViewModel
){
    LaunchedEffect(1) {
        val kidID = KidDataRepository.getKidData()!!.uid.toString()
        notificationsViewModel.checkIfChildExists(kidID)
        notificationsViewModel.fetchNotification()
    }
    // Observing LiveData
    val notifications by notificationsViewModel.kidNotifications.observeAsState()
    notifications.let {
            notification ->
        notification?.forEach{
            Log.d(ContentValues.TAG, "Notification: ${it!!.messageBody}")
            ItemNotificationsCard(
                it!!
            )
        }


    }
}

@Composable
fun ItemNotificationsCard(kidNotifications: KidNotifications) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .clip(RoundedCornerShape(16.dp))
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {

            val image: Painter = painterResource(id = R.drawable.kides_care)
            Image(
                modifier = Modifier
                    .size(80.dp, 80.dp)
                    .clip(RoundedCornerShape(8.dp)),
                painter = image,
                alignment = Alignment.Center,
                contentDescription = "",
                contentScale = ContentScale.Fit
            )

            Spacer(modifier = Modifier.width(16.dp))

            Column(modifier = Modifier
                .align(Alignment.CenterVertically)
                .fillMaxWidth()) {
                Log.d(TAG, "ItemNotificationsCard: $kidNotifications")
                Text(
                    text = kidNotifications.kidId.toString(),
                    modifier = Modifier.padding(0.dp, 0.dp, 12.dp, 0.dp),
                    color = Color(0xFF000000),
                    fontWeight = FontWeight.Bold,
                    style = MaterialTheme.typography.labelLarge,
                    fontSize = 24.sp
                )
                Spacer(modifier = Modifier.height(8.dp))

//                Text(
//                    text = buildString {
//                        append(parentNotifications.age)
//                        append("yrs | ")
//                        append(parentNotifications.)
//                    },
//                    modifier = Modifier.padding(0.dp, 0.dp, 12.dp, 0.dp),
//                    color = Color(0xff0000),
//                    style = typography.caption
//                )
                Text(
                    text = kidNotifications.messageBody.toString(),
                    modifier = Modifier
                        .padding(8.dp, 12.dp, 12.dp, 0.dp)
                        .align(AbsoluteAlignment.Right),
                    color =Color(0xFF000000),
                    style = MaterialTheme.typography.titleSmall
                )

            }

        }
    }
}
