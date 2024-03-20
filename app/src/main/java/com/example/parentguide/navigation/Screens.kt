package com.example.parentguide.navigation

sealed class Screens (val screen: String){
    data object Home : Screens("home")
    data object Notification : Screens("notification")

    data object Profile : Screens("profile")

    object CreateKidUser : Screens("create_kid_user") // Add this line
    object CustomItem : Screens("customitem") // Add this line

}

