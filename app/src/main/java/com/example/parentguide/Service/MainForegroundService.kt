package com.example.parentguide.Service

import MyFirebaseMessagingService
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.IBinder
import android.util.Log
import com.example.parentguide.Models.ParentNotifications
import com.example.parentguide.Notifications.NotificationsViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

class MainForegroundService : Service() {
    private var notifications: List<ParentNotifications> = emptyList()
    private val coroutineScope = CoroutineScope(Dispatchers.IO)
    private var notificationsJob: Job? = null

    // Assuming MyFirebaseMessagingService is properly imported and initialized
    private lateinit var myFirebaseMessagingService: MyFirebaseMessagingService
    private lateinit var notificationsViewModel: NotificationsViewModel

    override fun onCreate() {
        super.onCreate()
        // Initialize the ViewModel
        notificationsViewModel = NotificationsViewModel()
        // Optionally, start some data fetching or processing

    }
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        myFirebaseMessagingService = MyFirebaseMessagingService()  // Initializing here to ensure context availability
        startNotification()
        return START_STICKY
    }

    private fun startNotification() {
        notificationsJob?.cancel() // Cancel any existing notification job
        notificationsJob = coroutineScope.launch {
            while (isActive) {
                fetchNotificationText(applicationContext)
                delay(1000) // Check every second
            }
        }
    }

    private fun fetchNotificationText(context: Context) {
        val uid = FirebaseAuth.getInstance().currentUser?.uid ?: return
        val dbRef = FirebaseDatabase.getInstance().getReference("users")
        val parentRef = dbRef.child(uid).child("parentNotifications")

        parentRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val tempList = mutableListOf<ParentNotifications>()
                snapshot.children.forEach { childSnapshot ->
                    val notification = childSnapshot.getValue(ParentNotifications::class.java)
                    notification?.let {
                        if (it.isSend!!) {
                            // Define behavior for when isSend is true if needed
                        } else {
                            tempList.add(it)
                            myFirebaseMessagingService.showNotification(context, it.messageBody)
                            Log.d(TAG, "fetchNotificationText: Notification fetched: ${it.messageBody}")
                        }
                    }
                }
                sendNotification(tempList)
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e(TAG, "Error fetching notifications: ${error.message}")
            }
        })
    }

    private fun sendNotification(tempList: MutableList<ParentNotifications>) {
        notifications = tempList
        // Here you could expand on what happens with notifications, like updating UI or sending further alerts

    }

    override fun onDestroy() {
        notificationsJob?.cancel()
        coroutineScope.cancel()
        super.onDestroy()
    }

    override fun onBind(intent: Intent?): IBinder? = null

    companion object {
        private const val TAG = "MainForegroundService"
        const val SHOW_NOTIFICATIONS_EXTRA = "com.example.parentguide.SHOW_NOTIFICATIONS_EXTRA"

    }
}
