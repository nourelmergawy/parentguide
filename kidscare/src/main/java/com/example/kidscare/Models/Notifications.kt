package com.example.kidscare.Models

import java.util.Date
import java.util.concurrent.TimeUnit

data class Notifications (
   val parentNotifications : List<ParentNotifications>? = null,
    val kidNotifications :List<KidNotifications>?=  null
)

data class KidNotifications(
    val messageBody: String? = null,
    val date: String = null,
    val timeUnit: String,
)

data class ParentNotifications (
    val messageBody : String? = null,
    val date: Date? = null,
    val timeUnit: TimeUnit,
    )

