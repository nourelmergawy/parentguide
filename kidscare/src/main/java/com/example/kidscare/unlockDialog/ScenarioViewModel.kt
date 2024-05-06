package com.example.kidscare.unlockDialog

import android.content.ContentValues
import android.content.ContentValues.TAG
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.kidscare.Models.QuizData
import com.example.kidscare.Models.Scenario
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.coroutines.launch

class ScenarioViewModel: ViewModel()  {
    private val dbRef = FirebaseDatabase.getInstance().getReference("users")
    private var databaseReference: DatabaseReference =
        FirebaseDatabase.getInstance().getReference("scenarios")
    val _scenarios = MutableLiveData<List<Scenario>?>()
    init {
        // Optionally preload data or set an initial state
        _scenarios.value = null
    }

    fun getAllScenarios() {
        viewModelScope.launch {
            try {
                databaseReference.addValueEventListener(object : ValueEventListener {
                    override fun onDataChange(p0: DataSnapshot) {
                        val scenarios = mutableListOf<Scenario>()
                        for (childSnapshot in p0.children) {
                            val Scenario = childSnapshot.getValue(Scenario::class.java)
                            Scenario?.let { scenarios.add(it) }
                        }
                        _scenarios.value = scenarios
                    }

                    override fun onCancelled(p0: DatabaseError) {
                        Log.d(ContentValues.TAG, "onCancelled: ${p0.message}")
                    }

                })
            } catch (e: Exception) {
                Log.e(TAG, "Error fetching scenarios", e)
            }
        }
    }

}