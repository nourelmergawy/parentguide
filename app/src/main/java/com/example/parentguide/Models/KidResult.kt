package com.example.parentguide.Models

data class KidResult(
    val data: KidData?,
    val errorMessage: String?,
    )


data class KidData(
    val username: String ? = null,
    val password: String ? = null,
//    val pic : String ? = null,
    val age: Int? = null,
    val dailyLoginHours: Int? = null,
    val intialCoins: Int? = null,
   val gender :String ? = null
){
    // Add a no-argument constructor
    constructor() : this("", "0000") // Default values can be used if needed
}