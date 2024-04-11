package com.example.kidscare.Notification


data class SendNotificactionDto (
    val to : String ,
    val notificationBody : NotificationBody
)

data class NotificationBody (
    val title : String ,
    val body: String
)