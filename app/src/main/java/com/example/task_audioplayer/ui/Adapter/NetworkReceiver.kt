package com.example.task_audioplayer.ui.Adapter

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import androidx.compose.material.SnackbarDuration
import androidx.compose.material.SnackbarHostState
import androidx.compose.material.SnackbarResult
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

class NetworkReceiver(
    private val snackbarHostState: SnackbarHostState,
    private val coroutineScope: CoroutineScope,
) : BroadcastReceiver() {

    private var wasInternetAvailable = true

    override fun onReceive(context: Context, intent: Intent?) {
        val isInternetAvailable = isInternetAvailable(context)

        if (isInternetAvailable && !wasInternetAvailable) {

            coroutineScope.launch {
                snackbarHostState.currentSnackbarData?.dismiss()
                snackbarHostState.showSnackbar(
                    message = "Internet is back! Enjoy streaming.",
                    duration = SnackbarDuration.Short,
//                    backgroundColor = Color.Green,
//                    contentColor = Color.White
                )
            }
        } else if (!isInternetAvailable && wasInternetAvailable) {

            coroutineScope.launch {
                snackbarHostState.currentSnackbarData?.dismiss()
                val result = snackbarHostState.showSnackbar(
                    message = "Internet is off! Go to Downloads.",
                    actionLabel = "Go",
                    duration = SnackbarDuration.Indefinite
                )
                if (result == SnackbarResult.ActionPerformed) {
//                    navigateToDownloads()
                }
            }
        }

        wasInternetAvailable = isInternetAvailable
    }

    private fun isInternetAvailable(context: Context): Boolean {
        val connectivityManager =
            context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val activeNetwork = connectivityManager.activeNetwork ?: return false
        val networkCapabilities =
            connectivityManager.getNetworkCapabilities(activeNetwork) ?: return false
        return networkCapabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
    }
}
