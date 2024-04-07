package com.example.kidscare.navigation

sealed class Screens (val screen: String){
    data object Home : Screens("home")
    data object Notification : Screens("notification")

    data object Profile : Screens("profile")

     object CustomItem : Screens("customitem") // Add this line
     object KidHome : Screens("kidhome") // Add this line
      object QuizScreen : Screens("kidquiz/{quizId}") // Add this line
    object PermissionScreen : Screens("permission") // Add this line



}

