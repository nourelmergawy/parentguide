package com.example.kidscare.Models

data class ScenarioData(
    val description : String = "",
    val answer: String = "",
    val answers: Map<String, String> = emptyMap(),
    val image: String = "",
    val name: String = "",
    val question: String = "",
    val correct_answer: String= ""
)