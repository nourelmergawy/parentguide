package com.example.kidscare.navigation.quiz

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.kidscare.navigation.Model.QuizData
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class QuizViewModel : ViewModel() {
    private var databaseReference: DatabaseReference = FirebaseDatabase.getInstance().getReference("quizzes")
    private var _quiz = MutableLiveData<QuizData?>()  // Allow nullability for the initial value
    val quiz: LiveData<QuizData?> get() = _quiz

    init {
        // Optionally preload data or set an initial state
        _quiz.value = null
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
}