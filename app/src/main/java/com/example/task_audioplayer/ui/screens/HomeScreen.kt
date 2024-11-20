package com.example.task_audioplayer.ui.screens

import MediaPlayerCard
import SongCard
import SongsList
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
//import androidx.compose.foundation.layout.BoxScopeInstance.align
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.task_audioplayer.data.Song
import com.example.task_audioplayer.data.songsList
import com.example.task_audioplayer.ui.helper.DownloadHelper
import com.example.task_audioplayer.ui.helper.SongHelper

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun HomeScreen(songsList: List<Song>, innerContentPadding: PaddingValues) {
    val context = LocalContext.current
    var selectedSong by remember { mutableStateOf<Song?>(null) }
    val navController = rememberNavController()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(innerContentPadding)
            .background(Color.Transparent)
    ) {
        NavHost(
            navController = navController,
            startDestination = "home"
        ) {
            // Home Screen Composable
            composable("home") {
                SongsList(songsList = songsList, onSongSelected = { song ->
                    selectedSong = song
                    navController.navigate("song")
                })
            }

            // MediaPlayer Screen Composable
            composable("song") {
                selectedSong?.let { song ->
                    MediaPlayerCard(
                        modifier = Modifier
                            .fillMaxWidth()
                            .fillMaxHeight()
                            .align(Alignment.TopCenter)
                            .background(Color.Transparent),
                        song = song,
                        onDownloadClick = {
                            DownloadHelper.downloadMusic(context, song.media, song.title)
                        } ,
                        navController = navController
                    )
                }
            }
        }
    }
}



