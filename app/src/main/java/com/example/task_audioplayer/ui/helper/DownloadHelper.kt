package com.example.task_audioplayer.ui.helper

import android.app.DownloadManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.example.task_audioplayer.R
import java.io.File

object DownloadHelper {

    private const val CHANNEL_ID = "music_download_channel"

    @RequiresApi(Build.VERSION_CODES.O)
    fun downloadMusic(context: Context, url: String, title: String) {
        createNotificationChannel(context)

        val downloadDirectory = File(
            context.getExternalFilesDir(Environment.DIRECTORY_MUSIC),
            "MusicDownloads"
        )
        if (!downloadDirectory.exists()) {
            downloadDirectory.mkdirs()
        }

        val request = DownloadManager.Request(Uri.parse(url))
            .setTitle(title)
            .setDescription("Downloading $title")
            .setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
            .setAllowedOverMetered(true)
            .setAllowedOverRoaming(true)
            .setDestinationInExternalFilesDir(context, Environment.DIRECTORY_MUSIC, "MusicDownloads/$title.mp3")

        val downloadManager = context.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
        val downloadId = downloadManager.enqueue(request)

        val onComplete = object : BroadcastReceiver() {
            override fun onReceive(ctxt: Context, intent: Intent) {
                val id = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1)
                if (id == downloadId) {
                    Toast.makeText(context, "$title Download Complete", Toast.LENGTH_SHORT).show()
                    showDownloadCompleteNotification(context, title)
                    context.unregisterReceiver(this)
                }
            }
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            context.registerReceiver(onComplete, IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE), Context.RECEIVER_NOT_EXPORTED)
        } else {
            context.registerReceiver(onComplete, IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE),
                Context.RECEIVER_NOT_EXPORTED)
        }
    }


    private fun showDownloadCompleteNotification(context: Context, title: String) {
        val notificationBuilder = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.rounded_download_24)
            .setContentTitle("Download Complete")
            .setContentText("$title has been downloaded successfully.")
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)

        NotificationManagerCompat.from(context).notify(1, notificationBuilder.build())
    }

    private fun createNotificationChannel(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = "Music Downloads"
            val descriptionText = "Notifications for completed music downloads"
            val importance = NotificationManager.IMPORTANCE_HIGH
            val channel = NotificationChannel(CHANNEL_ID, name, importance).apply {
                description = descriptionText
            }
            // Register the channel with the system
            val notificationManager: NotificationManager =
                context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }
}
