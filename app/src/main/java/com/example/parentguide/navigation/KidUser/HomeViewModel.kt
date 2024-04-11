package com.example.parentguide.navigation.KidUser

import android.content.ContentValues.TAG
import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.ViewModel
import com.example.parentguide.Models.KidData
import com.google.android.gms.tasks.OnSuccessListener
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class HomeViewModel() : ViewModel() {
    private val _state = MutableStateFlow(HomeState())
    val state = _state.asStateFlow()
    private val _kidDataStateFlow = MutableStateFlow<DataState<List<KidData>>>(DataState.Empty)
    val kidDataStateFlow = _kidDataStateFlow.asStateFlow()

    private val _singleKidDataStateFlow = MutableStateFlow<DataState<KidData>>(DataState.Empty)
    val singleKidDataStateFlow = _singleKidDataStateFlow.asStateFlow()

    private val dbRef = FirebaseDatabase.getInstance().getReference("users")

    init {
        fetchDataFromFirebase()
    }


    private fun fetchDataFromFirebase() {
        // UID should be retrieved securely and correctly before this function is called.
        val uid = FirebaseAuth.getInstance().currentUser?.uid ?: return
        val userKidsRef = dbRef.child(uid).child("kidsUsers")

        userKidsRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val tempList = mutableListOf<KidData>()
                snapshot.children.forEach { childSnapshot ->
                    val kid = childSnapshot.getValue(KidData::class.java)
                    kid?.let { tempList.add(it) }
                }
                _kidDataStateFlow.value = DataState.Success(tempList)
            }

            override fun onCancelled(error: DatabaseError) {
                _kidDataStateFlow.value = DataState.Failure(error.message)
            }
        })
    }


    // Function to delete an item from the Firebase Realtime Database
    fun deleteItemFromFirebase(kidId: String,context:Context) {
        val uid = FirebaseAuth.getInstance().currentUser?.uid ?: return
        //    private val dbRef = FirebaseDatabase.getInstance().getReference("users")
        val kidRef = dbRef.child(uid).child("kidsUsers").child(kidId)

        // Remove the item from the database
        kidRef.removeValue().addOnSuccessListener(object :ValueEventListener, OnSuccessListener<Void> {
            override fun onDataChange(snapshot: DataSnapshot) {
                TODO("Not yet implemented")
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(context, error.message, Toast.LENGTH_LONG).show()
            }

            override fun onSuccess(p0: Void?) {

                Toast.makeText(context, "kid had been removed successfully", Toast.LENGTH_LONG).show()
            }

        })
    }
    fun addKidCoins(coins :Int?,kidId: String,context:Context){
        val uid = FirebaseAuth.getInstance().currentUser?.uid ?: return
        //    private val dbRef = FirebaseDatabase.getInstance().getReference("users")
        val kidRef =   dbRef.child(uid).child("kidsUsers").child(kidId)
        // Update the totalCoins field
        // Listener to read the totalCoins value
        kidRef.child("totalCoins").addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                // Get the totalCoins value
                val totalCoins = dataSnapshot.value as Long
                // Invoke the callback with the totalCoins value
                var newTotalCoins = totalCoins!! + coins!!
                Log.d(TAG, "newTotalCoins:$totalCoins ")
                updateCoins(newTotalCoins,kidId,context)
            }
            override fun onCancelled(databaseError: DatabaseError) {
                // Handle any errors
                Log.d("Error getting totalCoins value:", "${databaseError.toException()} ")
            }
        })
    }
    fun updateCoins(coins: Long, kidId: String, context:Context){
        val uid = FirebaseAuth.getInstance().currentUser?.uid ?: return
        //    private val dbRef = FirebaseDatabase.getInstance().getReference("users")
        val kidRef =   dbRef.child(uid).child("kidsUsers").child(kidId)
        kidRef.child("totalCoins").setValue(coins).addOnSuccessListener(
            object :ValueEventListener, OnSuccessListener<Void> {
                override fun onDataChange(snapshot: DataSnapshot) {
                    TODO("Not yet implemented")
                }

                override fun onCancelled(error: DatabaseError) {
                    Toast.makeText(context, error.message, Toast.LENGTH_LONG).show()
                }

                override fun onSuccess(p0: Void?) {

                    Toast.makeText(context, "coins added successfully", Toast.LENGTH_LONG).show()
                }

            }
        )
    }
    fun fetechKidDate(kidId: String) {
        val uid = FirebaseAuth.getInstance().currentUser?.uid ?: return
        dbRef.child(uid).child("kidsUsers").child(kidId)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    // Extract the QuizScore object directly from the snapshot if it exists
                    val tempList = mutableListOf<KidData>()

                    dataSnapshot.children.forEach { childSnapshot ->
                        val kid = childSnapshot.getValue(KidData::class.java)
                        kid?.let { tempList.add(it) }
                    }
                    Log.d(TAG, "onDataChange: ${tempList!!.get(0)}")
                    _singleKidDataStateFlow.value = DataState.Success(tempList!!.get(0))
                }

                override fun onCancelled(error: DatabaseError) {
                    _singleKidDataStateFlow.value = DataState.Failure(error.message)
                }

            })
    }
}


