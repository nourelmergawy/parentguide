package com.example.kidscare.navigation.permission



sealed class permissionState {
    class Success(val data: MutableList<String>) : permissionState()
    class Failure(val message: String) : permissionState()
    object Loading : permissionState()
    object Empty : permissionState()
}