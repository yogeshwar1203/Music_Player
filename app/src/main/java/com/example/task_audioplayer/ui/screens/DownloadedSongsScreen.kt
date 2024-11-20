package com.example.task_audioplayer.ui.screens

import android.content.Context
import android.media.MediaPlayer
import android.os.Environment
import android.util.Log
import androidx.compose.animation.expandVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import java.io.File
import java.util.concurrent.TimeUnit
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch


@Composable
fun DownloadedSongsScreen() {
    val context = LocalContext.current
    val downloadedSongs = remember { mutableStateOf(getDownloadedSongs(context)) }
    var selectedSong by remember { mutableStateOf<File?>(null) }
    var isPlaying by remember { mutableStateOf(false) }
    var mediaPlayer: MediaPlayer? by remember { mutableStateOf(null) }
    val coroutineScope = rememberCoroutineScope()
    var progress by remember { mutableStateOf(0f) }
    var currentTime by remember { mutableStateOf(0) }
    var totalTime by remember { mutableStateOf(0) }

    LaunchedEffect(selectedSong, isPlaying) {
        if (selectedSong != null && isPlaying) {
            mediaPlayer?.release()
            mediaPlayer = MediaPlayer().apply {
                setDataSource(selectedSong!!.path)
                prepare()
                start()
                totalTime = duration
                currentTime = currentPosition
            }

            coroutineScope.launch {
                while (isPlaying && mediaPlayer != null) {
                    delay(500L)
                    currentTime = mediaPlayer?.currentPosition ?: 0
                    progress = if (totalTime > 0) currentTime / totalTime.toFloat() else 0f
                }
            }
        }
    }

    DisposableEffect(Unit) {
        onDispose {
            mediaPlayer?.release()
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .fillMaxHeight()
            .padding(16.dp)
    ) {

        Column {


            Text(
                text = "Downloaded Songs",
                style = MaterialTheme.typography.h6,
                modifier = Modifier.padding(vertical = 8.dp)
            )

            if (downloadedSongs.value.isEmpty())
                Text(text = "No downloads")

            LazyColumn(
                modifier = Modifier.fillMaxSize()
            ) {
                items(downloadedSongs.value) { songFile ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                if (selectedSong == songFile) {
                                    if (isPlaying) {
                                        mediaPlayer?.pause()
                                        isPlaying = false
                                    } else {
                                        mediaPlayer?.start()
                                        isPlaying = true
                                    }
                                } else {
                                    selectedSong = songFile
                                    isPlaying = true
                                }
                            }
                            .padding(8.dp)
                    ) {
                        Icon(
                            imageVector = if (selectedSong == songFile && isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                            contentDescription = null,
                            modifier = Modifier.size(32.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(text = songFile.name, maxLines = 1, overflow = TextOverflow.Ellipsis)
                    }
                }
            }
        }

        // Media Player Card
        selectedSong?.let {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp , bottom = 50.dp)
                    .align(Alignment.BottomCenter)
                    .background(Color.LightGray, RoundedCornerShape(8.dp)),


                elevation = 8.dp
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Text(text = it.name, fontSize = 18.sp, maxLines = 1, overflow = TextOverflow.Ellipsis)

                    Slider(
                        value = progress,
                        onValueChange = { newValue ->
                            progress = newValue
                            val newPosition = (newValue * totalTime).toInt()
                            mediaPlayer?.seekTo(newPosition)
                            currentTime = newPosition
                        }
                    )


                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(text = formatTime(currentTime))
                        Text(text = formatTime(totalTime))
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    // Play/Pause Button
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        IconButton(onClick = {
                            if (isPlaying) {
                                mediaPlayer?.pause()
                            } else {
                                mediaPlayer?.start()
                            }
                            isPlaying = !isPlaying
                        }) {
                            Icon(
                                imageVector = if (isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                                contentDescription = "Play/Pause"
                            )
                        }
                    }
                }
            }
        }
    }
}

fun formatTime(milliseconds: Int): String {
    val minutes = TimeUnit.MILLISECONDS.toMinutes(milliseconds.toLong())
    val seconds = TimeUnit.MILLISECONDS.toSeconds(milliseconds.toLong()) % 60
    return String.format("%02d:%02d", minutes, seconds)
}


fun getDownloadedSongs(context: Context): List<File> {
    val musicDir = context.getExternalFilesDir(Environment.DIRECTORY_MUSIC)

    val musicDownloadDir = File(musicDir, "MusicDownloads")

    if (musicDownloadDir.exists() && musicDownloadDir.isDirectory) {
        return musicDownloadDir.listFiles()?.filter { it.isFile && it.extension == "mp3" } ?: emptyList()
    }

    return emptyList()
}
