package com.example.kidscare

import android.app.Activity
import android.app.AppOpsManager
import android.app.admin.DevicePolicyManager
import android.app.usage.UsageEvents
import android.app.usage.UsageStatsManager
import android.content.ComponentName
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Process
import android.os.UserManager
import android.provider.Settings
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.setContent
import androidx.activity.viewModels
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
import androidx.compose.runtime.collectAsState
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
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.example.kidscare.Models.KidData
import com.example.kidscare.Notification.Notification
import com.example.kidscare.Notification.NotificationsViewModel
import com.example.kidscare.navigation.Home.Home
import com.example.kidscare.navigation.Home.HomeState
import com.example.kidscare.navigation.Home.HomeViewModel
import com.example.kidscare.navigation.Kid.CustomItem
import com.example.kidscare.navigation.Kid.homeKidScreen
import com.example.kidscare.navigation.ProfileScreen
import com.example.kidscare.navigation.Screens
import com.example.kidscare.navigation.permission.AppLockScreen
import com.example.kidscare.navigation.permission.InstalledAppsList
import com.example.kidscare.navigation.permission.appblocker.ApplicationManagerViewModel
import com.example.kidscare.navigation.permission.appusage.AppUsageCheckWorker
import com.example.kidscare.navigation.permission.appusage.AppUsageViewModel
import com.example.kidscare.navigation.quiz.QuizScreen
import com.example.kidscare.navigation.quiz.QuizViewModel
import com.example.kidscare.service.MyDeviceAdminReceiver
import com.example.kidscare.signin.GoogleAuthUiClient
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.concurrent.TimeUnit
import kotlin.collections.set

data class BottomNavigationItem(
    val title: String,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector,
    val hasNews: Boolean,
    val badgeCount: Int? = null
)
object KidDataRepository {
    private var kidData: KidData? = null

    fun setKidData(data: KidData) {
        kidData = data
        Log.d(ContentValues.TAG, "setKidData: $kidData")
    }

    fun getKidData(): KidData? {
        return kidData
    }
}
class MainActivity2 : AppCompatActivity() {

    val googleAuthUiClient by lazy {
        GoogleAuthUiClient(
            context = applicationContext,
        )
    }
    private lateinit var applicationManagerViewModel: ApplicationManagerViewModel
    private lateinit var quizViewModel: QuizViewModel
    private lateinit var homeViewModel: HomeViewModel
    private val appUsageViewModel: AppUsageViewModel by viewModels {
        AppUsageViewModel.AppUsageViewModelFactory(
            this
        )
    }

    @RequiresApi(Build.VERSION_CODES.UPSIDE_DOWN_CAKE)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activateDeviceAdmin(this)
        checkAndRequestSystemAlertWindowPermission()
        scheduleAppUsageCheckWorker()
//        scheduleDeviceLock()
        if ( checkUsageStatsPermission() ) {
            // Implement further app logic here ...
            var foregroundAppPackageName : String? = null
            val currentTime = System.currentTimeMillis()
// The `queryEvents` method takes in the `beginTime` and `endTime` to retrieve the usage events.
// In our case, beginTime = currentTime - 10 minutes ( 1000 * 60 * 10 milliseconds )
// and endTime = currentTime
            val usageStatsManager = getSystemService(Context.USAGE_STATS_SERVICE) as UsageStatsManager

            val usageEvents = usageStatsManager.queryEvents( currentTime - (1000*60*10) , currentTime )
            val usageEvent = UsageEvents.Event()
            while ( usageEvents.hasNextEvent() ) {
                usageEvents.getNextEvent( usageEvent )
                Log.e( "usageEvent" , "${usageEvent.packageName} ${usageEvent.timeStamp}" )
            }
            val ioScope = CoroutineScope(Dispatchers.Unconfined)
            ioScope.launch {
                var apps = getNonSystemAppsList()
                Log.e( "APP-usage" , "${apps.values}" )
            }
        }
        else {
            // Navigate the user to the permission settings
            Intent( Settings.ACTION_USAGE_ACCESS_SETTINGS ).apply {
                startActivity( this )
            }
        }

        applicationManagerViewModel =
            ViewModelProvider(this).get(ApplicationManagerViewModel::class.java)
        appUsageViewModel.checkAppUsageAndLockIfNeeded("com.whatsapp",1)
        // Observe installedApps LiveData
        applicationManagerViewModel.installedApps.observe(this, Observer { installedAppsPair ->
            val (userAppsList, systemAppsList) = installedAppsPair
            // Here you can use the lists of installed apps as needed
            Log.d("MainActivity", "User Apps: $userAppsList")
            Log.d("MainActivity", "System Apps: $systemAppsList")
        })

        // Fetch installed apps
        applicationManagerViewModel.fetchInstalledApps(this)
        val notificationsViewModel = ViewModelProvider(this)[NotificationsViewModel::class.java]
        setContent {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize()
                ) {
                    quizViewModel = ViewModelProvider(this).get(QuizViewModel::class.java)
                    homeViewModel = ViewModelProvider(this).get(HomeViewModel::class.java)
                    val stateCreate by homeViewModel.state.collectAsStateWithLifecycle()
                    MyBottomAppBar(googleAuthUiClient= googleAuthUiClient,coroutineScope = lifecycleScope,homeViewModel,stateCreate,notificationsViewModel)
                }
//            MainAppScreen(appUsageViewModel)
        }

    }


    @RequiresApi(Build.VERSION_CODES.UPSIDE_DOWN_CAKE)
    @Composable
    fun MyBottomAppBar(
        googleAuthUiClient: GoogleAuthUiClient,
        coroutineScope: LifecycleCoroutineScope,
        homeViewModel: HomeViewModel,
        stateCreate: HomeState,
        notificationsViewModel: NotificationsViewModel
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
                                Text(text = item.title , color = Color.White)
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
                                        contentDescription = item.title,
                                        tint = if (index == selectedItemIndex) {
                                           Color.Black
                                        } else {
                                            Color.White
                                        }
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

                composable(Screens.Notification.screen){
                    Notification(notificationsViewModel = notificationsViewModel)
                }
                composable(Screens.Profile.screen) {
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
                                startActivity(Intent(applicationContext, MainActivity::class.java))
                                finish() // Finish current activity if necessary
                            }
                        }
                    )
                }

                composable(Screens.CustomItem.screen) {
                    CustomItem(viewModel = homeViewModel, navController = navController)
                }

                composable("kidquiz/{quizId}") { backStackEntry ->
                    QuizScreen(
                        quizViewModel = quizViewModel,
                        quizId = backStackEntry.arguments?.getString("quizId") ?: "",
                        navController =navController,
                        notificationsViewModel = notificationsViewModel
                    )
                }
                composable(Screens.KidHome.screen) {

                    homeKidScreen(quizViewModel,navController)
                }
                composable(Screens.PermissionScreen.screen) {

                    InstalledAppsList(applicationManagerViewModel,applicationContext,appUsageViewModel)
                }
            }
        }
    }
    // The `PACKAGE_USAGE_STATS` permission is a not a runtime permission and hence cannot be
// requested directly using `ActivityCompat.requestPermissions`. All special permissions
// are handled by `AppOpsManager`.
    private fun checkUsageStatsPermission() : Boolean {
        val appOpsManager = getSystemService(AppCompatActivity.APP_OPS_SERVICE) as AppOpsManager
        // `AppOpsManager.checkOpNoThrow` is deprecated from Android Q
        val mode = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            appOpsManager.unsafeCheckOpNoThrow(
                "android:get_usage_stats",
                Process.myUid(), packageName
            )
        }
        else {
            appOpsManager.checkOpNoThrow(
                "android:get_usage_stats",
                Process.myUid(), packageName
            )
        }
        return mode == AppOpsManager.MODE_ALLOWED
    }

    private fun getNonSystemAppsList() : Map<String,String> {
        val appInfos = packageManager.getInstalledApplications( PackageManager.GET_META_DATA )
        val appInfoMap = HashMap<String,String>()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            val userManager = getSystemService( Context.USER_SERVICE ) as UserManager
            if ( userManager.isUserUnlocked ) {
                // Access usage history ...
                for ( appInfo in appInfos ) {
                    if ( appInfo.flags != ApplicationInfo.FLAG_SYSTEM ) {
                        appInfoMap[ appInfo.packageName ]= packageManager.getApplicationLabel( appInfo ).toString()
                    }
                }
            }
        }else{
            for ( appInfo in appInfos ) {
                if ( appInfo.flags != ApplicationInfo.FLAG_SYSTEM ) {
                    appInfoMap[ appInfo.packageName ]= packageManager.getApplicationLabel( appInfo ).toString()
                }
            }
        }
        return appInfoMap
    }
    @Composable
    fun EnsureSystemAlertWindowPermission() {
        val context = LocalContext.current

        // Starting from Android M (6.0, API level 23), you need to check for permission
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (!Settings.canDrawOverlays(context)) {
                val intent = Intent(
                    Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                    Uri.parse("package:${context.packageName}")
                )
                context.startActivity(intent)
            }
        }
    }
    private fun checkAndRequestSystemAlertWindowPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (!Settings.canDrawOverlays(this)) {
                val intent = Intent(
                    Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                    Uri.parse("package:$packageName")
                )
                startActivity(intent)
            }
        }
    }

    fun activateDeviceAdmin(context: Context) {
        val componentName = ComponentName(context, MyDeviceAdminReceiver::class.java)
        val intent = Intent(DevicePolicyManager.ACTION_ADD_DEVICE_ADMIN).apply {
            putExtra(DevicePolicyManager.EXTRA_DEVICE_ADMIN, componentName)
            putExtra(DevicePolicyManager.EXTRA_ADD_EXPLANATION, "Explanation about what your app will do with device admin privileges.")
            if (context !is Activity) {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
        }
        context.startActivity(intent)
    }
    private fun scheduleAppUsageCheckWorker() {
        // Define the WorkRequest
        val checkUsageWorkRequest = PeriodicWorkRequestBuilder<AppUsageCheckWorker>(1, TimeUnit.HOURS)
            .build()

        // Enqueue the work with WorkManager
        WorkManager.getInstance(this).enqueue(checkUsageWorkRequest)

    }
    @Composable
    fun MainAppScreen(appUsageViewModel: AppUsageViewModel) {
        val isLocked = appUsageViewModel.lockScreen.collectAsState().value

        if (isLocked) {
            AppLockScreen()
        } else {

        }
    }


}
