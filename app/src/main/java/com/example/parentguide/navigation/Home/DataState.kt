package com.example.parentguide.navigation.Home

import com.example.parentguide.Models.KidData

sealed class DataState {
    class Success(val data: MutableList<KidData>) : DataState()
    class Failure(val message: String) : DataState()
    object Loading : DataState()
    object Empty : DataState()
}