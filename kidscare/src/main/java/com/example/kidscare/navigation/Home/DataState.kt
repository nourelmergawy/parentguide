package com.example.kidscare.navigation.Home

sealed class DataState<out T> {
    data class Success<T>(val data: T) : DataState<T>()
    data class Failure(val message: String) : DataState<Nothing>()
    object Loading : DataState<Nothing>()
    object Empty : DataState<Nothing>()
}
