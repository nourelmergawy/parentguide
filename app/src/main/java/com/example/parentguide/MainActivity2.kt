package com.example.parentguide
import android.os.Bundle
import android.widget.Toast
import androidx.activity.compose.setContent
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
import androidx.compose.material3.ExperimentalMaterial3Api
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.LifecycleCoroutineScope
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.parentguide.navigation.KidUser.CreateKidUser
import com.example.parentguide.navigation.KidUser.CustomItem
import com.example.parentguide.navigation.KidUser.Home
import com.example.parentguide.navigation.KidUser.HomeViewModel
import com.example.parentguide.navigation.KidUser.displayKid
import com.example.parentguide.navigation.Notification
import com.example.parentguide.navigation.ProfileScreen
import com.example.parentguide.navigation.Screens
import com.example.parentguide.presentaion.signin.GoogleAuthUiClient
import com.google.android.gms.auth.api.identity.Identity
import kotlinx.coroutines.launch

data class BottomNavigationItem(
    val title: String,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector,
    val hasNews: Boolean,
    val badgeCount: Int? = null
)
@OptIn(ExperimentalMaterial3Api::class)
class MainActivity2 : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val googleAuthUiClient by lazy {
            GoogleAuthUiClient(
                context = applicationContext,
                oneTapClient = Identity.getSignInClient(applicationContext)
            )
        }
        setContent {

                Surface(
                    modifier = Modifier.fillMaxSize(),
                ) {
                    MyBottomAppBar(googleAuthUiClient, coroutineScope = lifecycleScope)

                }
            }
        }
    }

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MyBottomAppBar(googleAuthUiClient: GoogleAuthUiClient,
                   coroutineScope: LifecycleCoroutineScope
) {
    val context = LocalContext.current.applicationContext

    val homeViewModel = viewModel<HomeViewModel>()

    val stateCreate by homeViewModel.state.collectAsStateWithLifecycle()

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
            NavigationBar {
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
            modifier = Modifier.padding(paddingValues)) {
            composable(Screens.Home.screen){

                Home(
                    state = stateCreate,
                    OncreateKidUserClick = {
                        coroutineScope.launch {
                            navController.navigate(Screens.CreateKidUser.screen)
                        }
                    }, viewModel = homeViewModel,
                    navController = navController
                )


            }
            composable(Screens.Notification.screen){ Notification() }
            composable("profile") {
                ProfileScreen(
                    userData = googleAuthUiClient.getSignedInUser(),
                    onSignOut = {
                        coroutineScope.launch {
                            googleAuthUiClient.signOut()
                            Toast.makeText(context
                                ,
                                "Signed out",
                                Toast.LENGTH_LONG
                            ).show()
                            navController.navigate("main")
                            navController.popBackStack()
                        }
                    }
                )
            }

            composable(Screens.CreateKidUser.screen) {
                CreateKidUser(
                    navController,

                )
            }
            composable(Screens.CustomItem.screen) {

                CustomItem(viewModel = homeViewModel,navController)
            }
            composable("kidScreen/{kidId}"){ backStackEntry ->
                displayKid(homeViewModel = homeViewModel, kidId =  backStackEntry.arguments?.getString("quizId") ?: "",navController = navController)
            }


        }
    }
}