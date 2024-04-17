
import android.annotation.SuppressLint
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat.getSystemService
import com.example.parentguide.MainActivity
import com.example.parentguide.R
import com.google.firebase.messaging.FirebaseMessagingService

class MyFirebaseMessagingService : FirebaseMessagingService() {
    private val channelId = 1
    private val channelName = "kids_care"
    private val channelDescription = "kids_careChannel_Description"
    private val notificationChannel = "kids_care_Channel_ID" // Make sure it matches the ID of the notification channel
    private val notificationTitle = "kids care"
    @SuppressLint("MissingPermission")
    fun showNotification(context: Context,notificationText:String?) {
        createNotificationChannel(context)
        val notification = createNotification(context,notificationText)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            context.startForegroundService(Intent(context, MyFirebaseMessagingService::class.java))
        } else {
            context.startService(Intent(context, MyFirebaseMessagingService::class.java))
        }

        val notificationManager = NotificationManagerCompat.from(context)
        notificationManager.notify(channelId, notification)
    }

    private fun createNotificationChannel(context: Context,) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

            val importance = NotificationManager.IMPORTANCE_DEFAULT

            val notificationChannel = NotificationChannel(notificationChannel, channelName, importance).apply {
                description = channelDescription
            }

            val notificationManager = getSystemService(context, NotificationManager::class.java)
            notificationManager?.createNotificationChannel(notificationChannel)
        }
    }

    private fun createNotification(context: Context,notificationText:String?): Notification {
        // Create an intent for launching your app's main activity when the notification is tapped
        val intent = Intent(context, MainActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT)

        // Build the notification using NotificationCompat.Builder
        val notificationBuilder = NotificationCompat.Builder(context, notificationChannel)
            .setContentTitle(notificationTitle)
            .setContentText(notificationText)
            .setSmallIcon(R.drawable.ic_logo) // Replace with your notification icon
            .setContentIntent(pendingIntent)
            .setAutoCancel(true) // Dismiss the notification when tapped

        // Return the built notification
        return notificationBuilder.build()
    }

}
