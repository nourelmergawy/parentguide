package com.example.parentguide.Notifications

import android.content.ContentValues.TAG
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.parentguide.Models.ParentNotifications
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class NotificationsViewModel : ViewModel() {

    private val dbRef = FirebaseDatabase.getInstance().getReference("users")
    private var _parentNotifications = MutableLiveData<List<ParentNotifications?>?>()  // Allow nullability for the initial value
    val parentNotifications: MutableLiveData<List<ParentNotifications?>?> get() = _parentNotifications

    init {
        _parentNotifications.value = null
    }

    fun checkIfChildExists() {
        val uid = FirebaseAuth.getInstance().currentUser?.uid ?: return
        dbRef.child(uid).child("Notifications").addListenerForSingleValueEvent(object : ValueEventListener {
            @RequiresApi(Build.VERSION_CODES.O)
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    Log.d(TAG, "Child exists.")
                } else {
                    Log.d(TAG, "Child does not exist.")
                    createInitialNotificationForUser(uid)
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
                Log.d(TAG, "Database error: ${databaseError.message}")
            }
        })
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun createInitialNotificationForUser(uid: String) {
        val currentDateTime = LocalDateTime.now()
        val formattedDate = currentDateTime.format(DateTimeFormatter.ofPattern("dd-MM-yyyy"))
        val formattedTime = currentDateTime.format(DateTimeFormatter.ofPattern("HH:mm"))

        val parentNotifications = mapOf(
            "parentNotifications" to mapOf(
                "0" to ParentNotifications(
                    "welcome to app",
                    formattedDate,
                    formattedTime
                )
            )
        )

        dbRef.child(uid).updateChildren(parentNotifications)
            .addOnSuccessListener {
                Log.d(TAG, "notifications: created")
            }
            .addOnFailureListener {
                Log.d(TAG, "notifications: error")
            }
    }
    private val _notification = MutableLiveData<ParentNotifications>()
    val notification: LiveData<ParentNotifications> = _notification

    fun fetchNotification(): MutableLiveData<List<ParentNotifications?>?> {
        val uid = FirebaseAuth.getInstance().currentUser?.uid
        dbRef.child(uid!!).child("parentNotifications")
        .addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val notifications = mutableListOf<ParentNotifications>()
                for (childSnapshot in snapshot.children) {
                    val notification = childSnapshot.getValue(ParentNotifications::class.java)
                    notification?.let { notifications.add(it) }
                }
                _parentNotifications.value = notifications
            }

            override fun onCancelled(error: DatabaseError) {
                Log.w(TAG, "Failed to read value.", error.toException())
            }
        })
        return _parentNotifications
    }

}
