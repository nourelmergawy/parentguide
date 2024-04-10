package com.example.kidscare.navigation.quiz

import android.content.ContentValues.TAG
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import androidx.core.content.ContextCompat
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.kidscare.KidDataRepository
import com.example.kidscare.Models.KidData
import com.example.kidscare.Models.QuizData
import com.example.kidscare.Models.QuizScore
import com.example.kidscare.permission.LockService
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class QuizViewModel : ViewModel() {
    private var databaseReference: DatabaseReference =
        FirebaseDatabase.getInstance().getReference("quizzes")
    private var _quiz = MutableLiveData<QuizData?>()  // Allow nullability for the initial value
    private var _quizScore = MutableLiveData<QuizScore?>()  // Allow nullability for the initial value

    val _quizzes = MutableLiveData<List<QuizData>?>()
    private val userDBRef = FirebaseDatabase.getInstance().getReference("users")

    val quiz: LiveData<QuizData?> get() = _quiz
    val quizzes: MutableLiveData<List<QuizData>?> get() = _quizzes
    val quizScore : LiveData<QuizScore?> get ()= _quizScore
    init {
        // Optionally preload data or set an initial state
        _quiz.value = null
        _quizzes.value = null
        _quizScore.value = null
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
    fun getQuizScore(quizId: String?){
        when(isQuizSolved(quizId)){
            "notSolved" -> {
                _quizScore.value = QuizScore(0, 0, "notSolved")
            }
            "solved" -> {
                _quizScore.value = KidDataRepository.getKidData()?.quizzes?.get(quizId!!.toInt())!!
            }
            "wrongAnswer" -> {
                _quizScore.value = KidDataRepository.getKidData()?.quizzes?.get(quizId!!.toInt())!!
            }
            else ->  _quizScore.value = QuizScore(0,0,"notSolved")
        }

    }
    fun isQuizSolved(quizId: String?) :String {
        val user = FirebaseAuth.getInstance().currentUser
        val uid = user?.uid
        var isQuizSolved = "notSolved"
        val kidData: KidData? = KidDataRepository.getKidData()
        val getKidId : String? = kidData?.uid
        Log.d(TAG, "isQuizSolved: $getKidId")
        // UID should be retrieved securely and correctly before this function is called.
        val userKidsRef =
            userDBRef.child(uid.toString()).child("kidsUsers").child(getKidId!!).child("quizzes")
        // Now, check for a specific quiz ID under each user's quizzes
        userKidsRef.get().addOnSuccessListener { quizzesSnapshot ->
            if (quizzesSnapshot.hasChild(quizId!!)) {
                if(quizzesSnapshot.child(quizId!!).child("hasSolved").value == true){
                    Log.d("QuizViewModel", "User has quiz ID $quizId ")
                    isQuizSolved = "solved"
                }else{
                    isQuizSolved = "wrongAnswer"
                }
            } else {
                Log.d("QuizViewModel", "User  does not have quiz ID $quizId")
                isQuizSolved = "notSolved"
            }
        }.addOnFailureListener { e ->
            Log.d("QuizViewModel", "Error checking quizzes for user", e)
            isQuizSolved = "Error"
        }
        return isQuizSolved
    }

    fun checkQuizAnswer(answer :Boolean,quizId: String?,quizScore: QuizScore){
        val user = FirebaseAuth.getInstance().currentUser
        val uid = user?.uid
        val kidData: KidData? = KidDataRepository.getKidData()
        val getKidId : String? = kidData?.uid
        val userKidsRef =
            userDBRef.child(uid.toString()).child("kidsUsers").child(getKidId!!).child("quizzes")
        Log.d(TAG, "checkQuizAnswer:${getKidId} ")
//        val quizScore = QuizScore(0,0,false)
        if (answer){
            when (isQuizSolved(quizId)){
                "wrongAnswer "-> {
                    quizScore.score = 5
                    quizScore.tryCount = quizScore.tryCount
                    quizScore.hasSolved = "solved"
                    userKidsRef.setValue(listOf(quizScore))
                }
                "notSolved" ->{
                    quizScore.score = 10
                    quizScore.tryCount = 0
                    quizScore.hasSolved = "solved"
                    userKidsRef.setValue(listOf(quizScore))
                }
                }
            }else{
                if ( quizScore.tryCount!! > 0) {

                    quizScore.score = 5
                    quizScore.tryCount = quizScore.tryCount?.plus(1)
                    quizScore.hasSolved = "wrongAnswer"
                    userKidsRef.setValue(listOf(quizScore))
                }else{
                    quizScore.score = 5
                    quizScore.tryCount = 1
                    quizScore.hasSolved = "wrongAnswer"
                    userKidsRef.setValue(listOf(quizScore))
                }
        }
    }
}