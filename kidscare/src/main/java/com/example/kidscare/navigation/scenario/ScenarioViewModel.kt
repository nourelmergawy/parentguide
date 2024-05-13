package com.example.kidscare.navigation.scenario

import android.content.ContentValues.TAG
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.kidscare.KidDataRepository
import com.example.kidscare.Models.QuizData
import com.example.kidscare.Models.ScenarioData
import com.example.kidscare.Models.ScenarioScore
import com.example.kidscare.navigation.permission.lockdevice.LockService
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

class ScenarioViewModel : ViewModel() {
    private var databaseReference: DatabaseReference = FirebaseDatabase.getInstance().getReference("scenarios")
    private var _scenario = MutableLiveData<ScenarioData?>()
    val _scenarios = MutableLiveData<List<ScenarioData>?>()

    private var _scenarioScore = MutableLiveData<ScenarioScore?>()

    val scenarios = MutableLiveData<List<ScenarioData>?>()
    private val userDBRef = FirebaseDatabase.getInstance().getReference("users")

    val scenario: LiveData<ScenarioData?> get() = _scenario
    val scenarioScore: LiveData<ScenarioScore?> get() = _scenarioScore

    init {
        _scenario.value = null
        scenarios.value = null
        _scenarioScore.value = null
    }

    fun loadAllScenarios(): MutableLiveData<List<ScenarioData>?> {
        databaseReference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val scenariosList = mutableListOf<ScenarioData>()
                for (childSnapshot in snapshot.children) {
                    val scenario = childSnapshot.getValue(ScenarioData::class.java)
                    scenario?.let { scenariosList.add(it) }
                }
                scenarios.value = scenariosList
            }

            override fun onCancelled(error: DatabaseError) {
                Log.d(TAG, "onCancelled: ${error.message}")
            }
        })
        return scenarios
    }

    fun loadScenario(scenarioId: String) {
        databaseReference.child(scenarioId).addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val scenario = dataSnapshot.getValue(ScenarioData::class.java)
                _scenario.value = scenario
            }

            override fun onCancelled(databaseError: DatabaseError) {
                Log.d(TAG, "onCancelled: ${databaseError.message}")
            }
        })
    }

    suspend fun getScenarioScore(scenarioId: String?) {
        try {
            val solvedStatus = isScenarioSolved(scenarioId)
            when (solvedStatus) {
                "notSolved" -> {
                    _scenarioScore.value = ScenarioScore(0, 0, "notSolved")
                }
                "solved", "wrongAnswer" -> {
                    val kidData = KidDataRepository.getKidData()
                    val scenario = kidData?.scenarios?.get(scenarioId?.toIntOrNull() ?: -1)
                    if (scenario != null) {
                        _scenarioScore.value = scenario
                    } else {
                        _scenarioScore.value = ScenarioScore(0, 0, "notSolved")
                    }
                }
                else -> {
                    _scenarioScore.value = ScenarioScore(0, 0, "notSolved")
                }
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error fetching scenario score: $e")
            _scenarioScore.value = ScenarioScore(0, 0, "notSolved")
        }
    }

    suspend fun isScenarioSolved(scenarioId: String?): String = withContext(Dispatchers.IO) {
        val user = FirebaseAuth.getInstance().currentUser
        val uid = user?.uid ?: return@withContext "Error"
        val userKidsRef = userDBRef.child(uid).child("kidsUsers").child("scenarios")

        suspendCoroutine<String> { continuation ->
            userKidsRef.child(scenarioId!!).addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    val scenarioScore = dataSnapshot.getValue(ScenarioScore::class.java)
                    val scenarioSolved = if (scenarioScore?.hasSolved == "solved") {
                        "solved"
                    } else {
                        "wrongAnswer"
                    }
                    continuation.resume(scenarioSolved)
                }

                override fun onCancelled(databaseError: DatabaseError) {
                    continuation.resumeWithException(databaseError.toException())
                }
            })
        }
    }

    fun handleWrongAnswer(context: Context) {
        val serviceIntent = Intent(context, LockService::class.java)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            ContextCompat.startForegroundService(context, serviceIntent)
        } else {
            context.startService(serviceIntent)
        }
    }
}