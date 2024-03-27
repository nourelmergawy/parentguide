package com.example.kidscare.navigation.Model

data class QuizData(
    val description : String = "",
    val answer: String = "",
    val answers: Map<String, String> = emptyMap(),
    val image: String = "",
    val name: String = "",
    val question: String = ""

    )