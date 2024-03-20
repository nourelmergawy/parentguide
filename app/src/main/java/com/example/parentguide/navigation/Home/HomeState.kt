package com.example.parentguide.navigation.Home

data class HomeState (
    val isCreateKidUserSuccessful: Boolean = false,
    val CreateKidUserError: String? = null
)