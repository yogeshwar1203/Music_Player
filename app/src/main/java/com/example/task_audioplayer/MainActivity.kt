package com.example.task_audioplayer

import android.content.IntentFilter
import android.content.pm.PackageManager
import android.net.ConnectivityManager
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.padding
import androidx.compose.material.BottomNavigation
import androidx.compose.material.BottomNavigationItem
import androidx.compose.material.Icon
import androidx.compose.material.Scaffold
import androidx.compose.material.SnackbarHost
import androidx.compose.material.SnackbarHostState
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.rememberScaffoldState
import androidx.compose.runtime.*
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.media3.exoplayer.offline.Download
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.task_audioplayer.ui.screens.DownloadedSongsScreen
import com.example.task_audioplayer.ui.screens.HomeScreen
import com.example.task_audioplayer.data.songsList
import com.example.task_audioplayer.ui.helper.NetworkReceiver
import com.example.task_audioplayer.ui.theme.MusicStreamingAppTheme
import java.util.jar.Manifest

class MainActivity : ComponentActivity() {
    private val snackbarHostState = SnackbarHostState()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Request permissions
        requestPermissions()
        setContent {
            MusicStreamingAppTheme {
                val navController = rememberNavController()
                val scaffoldState = rememberScaffoldState()
                var showDownloadsScreen by remember { mutableStateOf(false) }
                val coroutineScope = rememberCoroutineScope()


                // Register the network receiver
                val networkReceiver = NetworkReceiver(
                    snackbarHostState = snackbarHostState,
                    coroutineScope = coroutineScope,
                    navigateToDownloads = {
                        navController.navigate("downloads")
                    }
                )
                val intentFilter = IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION)
                registerReceiver(networkReceiver, intentFilter)


//                val networkReceiver = NetworkReceiver(
//                    snackbarHostState,
//                    coroutineScope,
//                    this
//                ) { showDownloadsScreen = true }
//
//                val intentFilter = IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION)
//                registerReceiver(networkReceiver, intentFilter)

                // Unregister the receiver when no longer needed
                DisposableEffect(Unit) {
                    onDispose {
                        unregisterReceiver(networkReceiver)
                    }
                }


                Scaffold(
                    scaffoldState = scaffoldState,
//                    topBar = {
//                        TopAppBar(
//                            title = { Text(text = "Music App") }
//                        )
//                    },
                    snackbarHost = { SnackbarHost(snackbarHostState) },
                    bottomBar = {
                        BottomNavigation {
                            BottomNavigationItem(
                                icon = { Icon(Icons.Default.Home, contentDescription = "Home") },
                                label = { Text("Home") },
                                selected = navController.currentDestination?.route == "home",
                                onClick = { navController.navigate("home") }
                            )
                            BottomNavigationItem(
                                icon = { Icon(Icons.Default.Download, contentDescription = "Downloads") },
                                label = { Text("Downloads") },
                                selected = navController.currentDestination?.route == "downloads",
                                onClick = { navController.navigate("downloads") }
                            )
                        }
                    }
                ) { innerPadding ->

                    if (showDownloadsScreen) {
                        navController.navigate("downloads")
                    } else {
//                        HomeScreen(songsList = songsList, innerPadding)
                        NavHost(
                            navController = navController,
                            startDestination = "home"
                        ) {
                            composable("home") { HomeScreen(songsList, innerPadding) }
                            composable("downloads") { DownloadedSongsScreen() }
                        }
                    }

                }
            }
        }
    }

    private fun requestPermissions() {
        val permissionsToRequest = mutableListOf<String>()

        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.INTERNET) != PackageManager.PERMISSION_GRANTED) {
            permissionsToRequest.add(android.Manifest.permission.INTERNET)
        }
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_NETWORK_STATE) != PackageManager.PERMISSION_GRANTED) {
            permissionsToRequest.add(android.Manifest.permission.ACCESS_NETWORK_STATE)
        }
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            permissionsToRequest.add(android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
        }
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            permissionsToRequest.add(android.Manifest.permission.READ_EXTERNAL_STORAGE)
        }

        if (permissionsToRequest.isNotEmpty()) {
            ActivityCompat.requestPermissions(
                this,
                permissionsToRequest.toTypedArray(),
                PERMISSION_REQUEST_CODE
            )
        }
    }

    companion object {
        private const val PERMISSION_REQUEST_CODE = 100
    }

}

