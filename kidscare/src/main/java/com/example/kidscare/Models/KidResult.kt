package com.example.kidscare.Models

data class KidResult(
    val data: KidData?,
    val errorMessage: String?,
    )


data class KidData(
    val uid:  String ? = null,
    val username: String ? = null,
    val password: String ? = null,
    val age: Int? = null,
    val dailyLoginHours: Int? = null,
    val intialCoins: Int? = null,
    val gender:String ? = null,
    val totalCoins: Int?= null,
    val quizzes: List<QuizScore>?= null,

    ){
    // Add a no-argument constructor
    constructor() : this("", "0000") // Default values can be used if needed
}
data class QuizScore(
    var score : Int? = 0,
    var tryCount :Int? = 0,
    var hasSolved : String ? = null,
)