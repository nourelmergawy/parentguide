package com.example.parentguide.Models

data class NotificationsSate(
    val data: ParentNotifications?,
    val errorMessage: String?,
)

data class Notifications (
   val parentNotifications : List<ParentNotifications>? = null,
    val kidNotifications :List<KidNotifications>?=  null
)

data class KidNotifications(
    val kidId : String? =null,
    val messageBody: String? = null,
    val date: String? = null,
    val timeUnit: String? = null,
    val isSend :Boolean?= false
)

data class ParentNotifications(
    val messageBody: String? = null,
    val date: String? = null,
    val timeUnit: String? = null,
    val isSend :Boolean?= false

)

