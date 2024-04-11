package com.example.kidscare.Notification

data class NotificationState(
    val isEnteringToken : Boolean = true,
    val remoteToken : String = "",
    val notificationText : String = "",
)
