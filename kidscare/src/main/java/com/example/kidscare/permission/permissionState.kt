package com.example.kidscare.permission



sealed class permissionState {
    class Success(val data: MutableList<String>) : permissionState()
    class Failure(val message: String) : permissionState()
    object Loading : permissionState()
    object Empty : permissionState()
}