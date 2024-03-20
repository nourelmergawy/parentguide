package com.example.parentguide.Models

data class KidResult(
    val data: KidData?,
    val errorMessage: String?,
    )


data class KidData(
    val username : String ? = null,
    val password : String ? = null,
//    val pic : String ? = null,
    val age : String ? = null,
    val dailyLoginHours : String ? = null,
    val intialCoins : String ? = null,
//    val Gender :Boolean
){
    // Add a no-argument constructor
    constructor() : this("", "0000") // Default values can be used if needed
}