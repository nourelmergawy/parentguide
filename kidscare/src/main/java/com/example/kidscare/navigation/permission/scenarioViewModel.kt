package com.example.kidscare.navigation.permission

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class scenarioViewModel : ViewModel() {
    private val _showDialog = MutableStateFlow(false) // Initially false
    val showDialog: StateFlow<Boolean> = _showDialog

    fun onUserPresent() {
        _showDialog.value = true  // Update the state to show the dialog
    }

    fun dismissDialog() {
        _showDialog.value = false // Update the state to hide the dialog
    }
}