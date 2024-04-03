package com.example.kidscare

import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.activity.compose.setContent
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.LifecycleCoroutineScope
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.kidscare.navigation.Home.CustomItem
import com.example.kidscare.navigation.Home.Home
import com.example.kidscare.navigation.Home.HomeState
import com.example.kidscare.navigation.Home.HomeViewModel
import com.example.kidscare.navigation.Home.homeKidScreen
import com.example.kidscare.navigation.Notification
import com.example.kidscare.navigation.Screens
import com.example.kidscare.navigation.quiz.QuizScreen
import com.example.kidscare.navigation.quiz.QuizViewModel
import com.example.kidscare.permission.ApplicationManagerViewModel
import com.example.kidscare.permission.InstalledAppsList
import com.google.firebase.auth.FirebaseAuth

data class BottomNavigationItem(
    val title: String,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector,
    val hasNews: Boolean,
    val badgeCount: Int? = null
)
class MainActivity2 : AppCompatActivity() {
    private lateinit var applicationManagerViewModel: ApplicationManagerViewModel

    //    private lateinit var quizViewModel : QuizViewModel
    private lateinit var quizViewModel: QuizViewModel
    private lateinit var homeViewModel: HomeViewModel

    @RequiresApi(Build.VERSION_CODES.UPSIDE_DOWN_CAKE)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        applicationManagerViewModel =
            ViewModelProvider(this).get(ApplicationManagerViewModel::class.java)

        // Observe installedApps LiveData
        applicationManagerViewModel.installedApps.observe(this, Observer { installedAppsPair ->
            val (userAppsList, systemAppsList) = installedAppsPair
            // Here you can use the lists of installed apps as needed
            Log.d("MainActivity", "User Apps: $userAppsList")
            Log.d("MainActivity", "System Apps: $systemAppsList")
        })

        // Fetch installed apps
        applicationManagerViewModel.fetchInstalledApps(this)
        setContent {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize()
                ) {
                    val uid = FirebaseAuth.getInstance().currentUser?.uid

                    quizViewModel = ViewModelProvider(this).get(QuizViewModel::class.java)
//                    QuizScreen(quizViewModel)
                    homeViewModel = ViewModelProvider(this).get(HomeViewModel::class.java)
                    val stateCreate by homeViewModel.state.collectAsStateWithLifecycle()
                    MyBottomAppBar( coroutineScope = lifecycleScope,homeViewModel,stateCreate)
//                    CustomItem(viewModel = homeViewModel)
                }
        }
    }
    @RequiresApi(Build.VERSION_CODES.UPSIDE_DOWN_CAKE)
    @Composable
    fun MyBottomAppBar(
        coroutineScope: LifecycleCoroutineScope,
        homeViewModel: HomeViewModel,
        stateCreate: HomeState
    ) {
        val context = LocalContext.current.applicationContext


        val navController = rememberNavController()
        var selectedItemIndex by rememberSaveable {
            mutableStateOf(1)
        }
        val items = listOf(
            BottomNavigationItem(
                title = "Notification",
                selectedIcon = Icons.Filled.Notifications,
                unselectedIcon = Icons.Outlined.Notifications,
                hasNews = false,
                badgeCount = 1
            ),
            BottomNavigationItem(
                title = "Home",
                selectedIcon = Icons.Filled.Home,
                unselectedIcon = Icons.Outlined.Home,
                hasNews = false,
            ),

            BottomNavigationItem(
                title = "Profile",
                selectedIcon = Icons.Filled.Person,
                unselectedIcon = Icons.Outlined.Person,
                hasNews = true,
            ),
        )
        Scaffold(
            bottomBar = {
                NavigationBar(containerColor = Color(0xff083A2B)){
                    items.forEachIndexed { index, item ->
                        NavigationBarItem(
                            selected = selectedItemIndex == index,
                            onClick = {
                                selectedItemIndex = index
                                navController.navigate(item.title)
                            },
                            label = {
                                Text(text = item.title)
                            },
                            alwaysShowLabel = false,
                            icon = {
                                BadgedBox(
                                    badge = {
                                        if(item.badgeCount != null) {
                                            Badge {
                                                Text(text = item.badgeCount.toString())
                                            }
                                        } else if(item.hasNews) {
                                            Badge()
                                        }
                                    }
                                ) {
                                    Icon(
                                        imageVector = if (index == selectedItemIndex) {
                                            item.selectedIcon
                                        } else item.unselectedIcon,
                                        contentDescription = item.title
                                    )
                                }
                            }
                        )
                    }
                }
            }
        ) {
                paddingValues ->
            NavHost(navController = navController,
                startDestination = Screens.Home.screen,
                modifier = Modifier.padding(paddingValues)
            ) {
                composable(Screens.Home.screen){
                    Home(viewModel = homeViewModel
                        ,navController = navController
                       )
                }
                composable(Screens.Notification.screen){ Notification() }
//                composable("profile") {
//                    ProfileScreen(
//                        userData = googleAuthUiClient.getSignedInUser(),
//                        onSignOut = {
//                            coroutineScope.launch {
//                                googleAuthUiClient.signOut()
//                                Toast.makeText(context
//                                    ,
//                                    "Signed out",
//                                    Toast.LENGTH_LONG
//                                ).show()
//                                navController.navigate("main")
//                                navController.popBackStack()
//                            }
//                        }
//                    )
//                }


                composable(Screens.CustomItem.screen) {
                    CustomItem(viewModel = homeViewModel, navController = navController)
                }

                composable(Screens.QuizScreen.screen) {

                    QuizScreen(quizViewModel)
                }
                composable(Screens.KidHome.screen) {

                    homeKidScreen(quizViewModel,navController)
                }
                composable(Screens.PermissionScreen.screen) {

                    InstalledAppsList(applicationManagerViewModel)
                }
            }
        }
    }
}
