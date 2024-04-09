package com.example.kidscare.navigation.quiz

import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.content.ContextCompat
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.kidscare.Models.QuizData
import com.example.kidscare.permission.LockService
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class QuizViewModel : ViewModel() {
    private var databaseReference: DatabaseReference = FirebaseDatabase.getInstance().getReference("quizzes")
    private var _quiz = MutableLiveData<QuizData?>()  // Allow nullability for the initial value
    val _quizzes = MutableLiveData<List<QuizData>?>()
    val quiz: LiveData<QuizData?> get() = _quiz
    val quizzes: MutableLiveData<List<QuizData>?> get() = _quizzes

    init {
        // Optionally preload data or set an initial state
        _quiz.value = null
        _quizzes.value = null
    }
    fun loadAllQuiz(): MutableLiveData<List<QuizData>?> {
        databaseReference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val quizzes = mutableListOf<QuizData>()
                for (childSnapshot in snapshot.children) {
                    val quiz = childSnapshot.getValue(QuizData::class.java)
                    quiz?.let { quizzes.add(it) }
                }
                _quizzes.value = quizzes
            }

            override fun onCancelled(error: DatabaseError) {
                // Handle possible errors
            }
        })

        return _quizzes
    }

    fun loadQuiz(quizId: String) {
        databaseReference.child(quizId).addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val quiz = dataSnapshot.getValue(QuizData::class.java)
                _quiz.value = quiz  // No need to force non-null with !! operator
            }

            override fun onCancelled(databaseError: DatabaseError) {
                // Handle possible errors, maybe set a specific state or log an error
            }
        })
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