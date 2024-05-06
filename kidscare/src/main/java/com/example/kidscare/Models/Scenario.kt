package com.example.kidscare.Models

import com.google.firebase.database.IgnoreExtraProperties

@IgnoreExtraProperties
data class Scenario(
    val title: String = "",
    val content: String = "",
    val image: String = "",
    val answers: List<String> = emptyList(),
    val recommended: String = ""
)