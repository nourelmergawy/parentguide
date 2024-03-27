package com.example.kidscare.navigation.Home

import android.content.ContentValues.TAG
import android.util.Log
import androidx.lifecycle.ViewModel
import com.example.kidscare.Models.KidData
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

    private val dbRef = FirebaseDatabase.getInstance().getReference("users")

    init {
        fetchDataFromFirebase()
    }


    private fun fetchDataFromFirebase() {
        val user = FirebaseAuth.getInstance().currentUser
        val uid = user?.uid

        // UID should be retrieved securely and correctly before this function is called.
        val userKidsRef = dbRef.child(uid.toString()).child("kidsUsers")

        userKidsRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val tempList = mutableListOf<KidData>()
                snapshot.children.forEach { childSnapshot ->

                    val kid = childSnapshot.getValue(KidData::class.java)
                    Log.d(TAG, "onDataChangekid: ${kid}")
                    kid?.let { tempList.add(it) }
                }
                _kidDataStateFlow.value = DataState.Success(tempList)
            }

            override fun onCancelled(error: DatabaseError) {
                _kidDataStateFlow.value = DataState.Failure(error.message)
            }
        })
    }
}


