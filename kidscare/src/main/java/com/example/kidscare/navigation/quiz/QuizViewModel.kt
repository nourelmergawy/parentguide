package com.example.kidscare.navigation.quiz

import MyFirebaseMessagingService
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
import com.example.kidscare.Models.KidData
import com.example.kidscare.Models.QuizData
import com.example.kidscare.Models.QuizScore
import com.example.kidscare.navigation.permission.lockdevice.LockService
import com.google.android.gms.tasks.OnSuccessListener
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

class QuizViewModel : ViewModel() {
    private val myFirebaseMessagingService = MyFirebaseMessagingService()

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
                Log.d(TAG, "onCancelled: ${error.message}")
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
                Log.d(TAG, "onCancelled: ${databaseError.message}")
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
    suspend fun getQuizScore(quizId: String?) {
        try {
            val solvedStatus = isQuizSolved(quizId)
            when (solvedStatus) {
                "notSolved" -> {
                    _quizScore.value = QuizScore(0, 0, "notSolved")
                }
                "solved", "wrongAnswer" -> {
                    val kidData = KidDataRepository.getKidData()
                    val quiz = kidData?.quizzes?.get(quizId?.toIntOrNull() ?: -1)
                    if (quiz != null) {
                        _quizScore.value = quiz
                    } else {
                        // Quiz not found, handle the error accordingly
                        _quizScore.value = QuizScore(0, 0, "notSolved")
                    }
                }
                else -> {
                    // Handle other cases
                    _quizScore.value = QuizScore(0, 0, "notSolved")
                }
            }
        } catch (e: Exception) {
            // Handle exception
            _quizScore.value = QuizScore(0, 0, "notSolved")
        }
    }
    // Define a suspend function to fetch quiz solved status
    suspend fun isQuizSolved(quizId: String?): String = withContext(Dispatchers.IO) {
        val user = FirebaseAuth.getInstance().currentUser
        val uid = user?.uid
        val kidData: KidData? = KidDataRepository.getKidData()
        val getKidId: String? = kidData?.uid
        var quizSolved = ""
        Log.d(TAG, "isQuizSolved: $getKidId")
        val userKidsRef =
            userDBRef.child(uid.toString()).child("kidsUsers").child(getKidId!!).child("quizzes")

        // Use suspendCoroutine to convert the Firebase API to a suspend function
        suspendCoroutine<String> { continuation ->
            userKidsRef.child(quizId!!).addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    // Extract the QuizScore object directly from the snapshot if it exists
                    val quizScore = dataSnapshot.getValue(QuizScore::class.java)

                    quizSolved = if (quizScore?.hasSolved == "solved") {
                        Log.d("QuizViewModel", "User has quiz ID $quizId and it is solved")
                        "solved"
                    } else {
                        Log.d("QuizViewModel", "User has quiz ID $quizId but it has wrong answers or not attempted")
                        "wrongAnswer"
                    }

                    // Resume the coroutine with the quizSolved value
                    continuation.resume(quizSolved)
                }


                override fun onCancelled(databaseError: DatabaseError) {
                    Log.d("QuizViewModel", "Error checking quizzes for user", databaseError.toException())
                    quizSolved = "Error"
                    // Resume the coroutine with an exception
                    continuation.resumeWithException(databaseError.toException())
                }
            })
        }
    }

    suspend fun checkQuizAnswer(answer: Boolean, quizId: String?, quizScore: QuizScore) {
        try {
            val user = FirebaseAuth.getInstance().currentUser
            val uid = user?.uid
            val kidData: KidData? = KidDataRepository.getKidData()
            val getKidId: String? = kidData?.uid
            val userKidsRef: DatabaseReference = userDBRef.child(uid.toString()).child("kidsUsers").child(getKidId!!).child("quizzes")
            Log.d(TAG, "checkQuizAnswer: $getKidId")

            if (answer) {
                when (isQuizSolved(quizId)) {
                    "wrongAnswer" -> {
                        quizScore.score = 5
                        quizScore.tryCount = quizScore.tryCount
                        quizScore.hasSolved = "solved"
                        userKidsRef.child(quizId!!).setValue(quizScore).await()
                    }
                    "notSolved" -> {
                        quizScore.score = 10
                        quizScore.tryCount = 0
                        quizScore.hasSolved = "solved"
                        userKidsRef.child(quizId!!).setValue(quizScore).await()
                    }
                }
            } else {
                if (quizScore.tryCount!! > 0) {
                    quizScore.score = 5
                    quizScore.tryCount = quizScore.tryCount?.plus(1)
                    quizScore.hasSolved = "wrongAnswer"
                    userKidsRef.child(quizId!!).setValue(quizScore).await()
                } else {
                    quizScore.score = 5
                    quizScore.tryCount = 1
                    quizScore.hasSolved = "wrongAnswer"
                    userKidsRef.child(quizId!!).setValue(quizScore).await()
                }
            }
        } catch (e: Exception) {
            // Handle the exception
            Log.e(TAG, "Error checking quiz answer: $e")
        }
    }
    fun lockDeviceNotification(context: Context,notificationText:String?){
        myFirebaseMessagingService.showNotification(context,notificationText)
    }

    private val dbRef = FirebaseDatabase.getInstance().getReference("users")

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
}