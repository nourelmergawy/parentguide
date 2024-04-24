package com.example.kidscare.Notification

import android.content.ContentValues
import android.content.ContentValues.TAG
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.kidscare.Models.KidNotifications
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class NotificationsViewModel  : ViewModel() {

    private val dbRef = FirebaseDatabase.getInstance().getReference("users")
    private var _kidNotifications = MutableLiveData<List<KidNotifications?>?>()  // Allow nullability for the initial value
    val kidNotifications: MutableLiveData<List<KidNotifications?>?> get() = _kidNotifications

    init {
        _kidNotifications.value = null
    }

    fun checkIfChildExists(kidId:String) {
        val uid = FirebaseAuth.getInstance().currentUser?.uid ?: return
        dbRef.child(uid).child("kidsUsers").child(kidId).addListenerForSingleValueEvent(object :
            ValueEventListener {
            @RequiresApi(Build.VERSION_CODES.O)
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    Log.d(ContentValues.TAG, "Child exists.")
                } else {
                    Log.d(ContentValues.TAG, "Child does not exist.")
                    createInitialNotificationForUser(uid,kidId)
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
                Log.d(ContentValues.TAG, "Database error: ${databaseError.message}")
            }
        })
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun createInitialNotificationForUser(uid: String,kidId:String) {
        val currentDateTime = LocalDateTime.now()
        val formattedDate = currentDateTime.format(DateTimeFormatter.ofPattern("dd-MM-yyyy"))
        val formattedTime = currentDateTime.format(DateTimeFormatter.ofPattern("HH:mm"))

        val kidtifications = mapOf(
            "kidNotifications" to mapOf(
                "0" to KidNotifications(
                    "welcome to app",
                    formattedDate,
                    formattedTime
                )
            )
        )

        dbRef.child(uid).child("kidsUsers").child(kidId).child("Notifications").updateChildren(kidtifications)
            .addOnSuccessListener {
                Log.d(ContentValues.TAG, "notifications: created")
            }
            .addOnFailureListener {
                Log.d(ContentValues.TAG, "notifications: error")
            }
    }
    @RequiresApi(Build.VERSION_CODES.O)
     fun createNotification(kidId:String) {
        val uid = FirebaseAuth.getInstance().currentUser?.uid ?: return
        val currentDateTime = LocalDateTime.now()
        val formattedDate = currentDateTime.format(DateTimeFormatter.ofPattern("dd-MM-yyyy"))
        val formattedTime = currentDateTime.format(DateTimeFormatter.ofPattern("HH:mm"))

        val kidtifications :Map<String, KidNotifications> = mapOf("0" to KidNotifications(
        "welcome to app",
        formattedDate,
        formattedTime
        ))

        dbRef.child(uid).child("kidsUsers").child(kidId).child("Notifications").updateChildren(kidtifications)
            .addOnSuccessListener {
                Log.d(ContentValues.TAG, "notifications: created")
            }
            .addOnFailureListener {
                Log.d(ContentValues.TAG, "notifications: error")
            }
    }
    private val _notification = MutableLiveData<KidNotifications>()
    val notification: LiveData<KidNotifications> = _notification
    fun fetchNotification(): MutableLiveData<List<KidNotifications?>?> {
        val uid = FirebaseAuth.getInstance().currentUser?.uid
        dbRef.child(uid!!).child("parentNotifications")
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val notifications = mutableListOf<KidNotifications>()
                    for (childSnapshot in snapshot.children) {
                        val notification = childSnapshot.getValue(KidNotifications::class.java)
                        notification?.let { notifications.add(it) }
                    }
                    _kidNotifications.value = notifications
                }

                override fun onCancelled(error: DatabaseError) {
                    Log.w(TAG, "Failed to read value.", error.toException())
                }
            })
        return _kidNotifications
    }
    fun fetchNotification(kidId: String): MutableLiveData<List<KidNotifications?>?> {
        val uid = FirebaseAuth.getInstance().currentUser?.uid
        dbRef.child(uid!!).child("kidsUsers").child(kidId).child("Notifications")
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val notifications = mutableListOf<KidNotifications>()
                    for (childSnapshot in snapshot.children) {
                        val notification = childSnapshot.getValue(KidNotifications::class.java)
                        notification?.let {
                            Log.d(TAG, "fetchNotification: $notifications")
                            notifications.add(it) }
                    }
                    _kidNotifications.value = notifications
                }

                override fun onCancelled(error: DatabaseError) {
                    Log.w(TAG, "Failed to read value.", error.toException())
                }
            })
        return _kidNotifications
    }
}