package com.example.task_audioplayer

import android.Manifest.permission.POST_NOTIFICATIONS
import android.app.PendingIntent
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.media.MediaPlayer
import android.media.MediaSession2Service.MediaNotification
import android.os.Binder
import android.os.Build
import android.os.IBinder
import android.support.v4.media.session.MediaSessionCompat
import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.core.app.NotificationCompat
import androidx.core.app.PendingIntentCompat
import androidx.core.content.ContextCompat
import androidx.media3.extractor.mp4.Track
import com.example.task_audioplayer.data.Song
import com.example.task_audioplayer.data.songsList
import com.google.common.util.concurrent.Service
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import okhttp3.Dispatcher


const val PREV  = "prev"
const val NEXT = "next"
const val PLAY_PAUSE = "play_pause"

class MusicPlayerService : android.app.Service() {

    private var mediaPlayer = MediaPlayer()

    var currentMusic = MutableStateFlow<Song>(Song())

    var musiclist = mutableListOf<Song>()

    val maxduration = MutableStateFlow(0f)
    val currentDuration = MutableStateFlow(0f)

    val isplaying = MutableStateFlow<Boolean>(false)

    val binder  = MusicBinder()

    inner class MusicBinder : Binder() {

        fun getService() = this@MusicPlayerService

        fun setmusiclist(list : List<Song>){
            this@MusicPlayerService.musiclist = list.toMutableList()
        }

        fun currentDuration() = this@MusicPlayerService.currentDuration

        fun maxDuration() = this@MusicPlayerService.maxduration

        fun isplaying() = this@MusicPlayerService.isplaying

        fun currentMusic() = this@MusicPlayerService.currentMusic

    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {

        intent?.let {
            when (it.action) {
                PREV -> prev()
                NEXT -> next()
                PLAY_PAUSE -> play_pause()
                else -> {
                    // Default action if no valid action is provided
                    if (musiclist.isNotEmpty()) {
                        val pos = it.getIntExtra("song" , -1)
                        Log.e("pos" , pos.toString())
                        if(pos != -1) {
                            currentMusic.update { songsList.get(pos) }
                        }
                            play(currentMusic.value)

                    } else{
                        Log.e("pos" , "in else")
                    }
                }
            }
        }

        return START_STICKY
    }


    override fun onBind(intent: Intent?): IBinder? {

        intent?.let {
            when(intent.action){
                PREV -> {
                    prev()
                }

                NEXT -> {
                    next()
                }

                PLAY_PAUSE -> {
                    play_pause()
                }

                else -> {
                    currentMusic.update { songsList.get(0) }
                    play(currentMusic.value)
                }

            }
        }
        return binder
    }

    fun setCurrentMusic(song: Song) {
        currentMusic.value = song
        mediaPlayer?.reset()
        // Prepare and play the new song
        play(song)
    }

    fun next(){
        stopUpdatingProgress()
        mediaPlayer.reset()
        mediaPlayer = MediaPlayer()

        val index = songsList.indexOf(currentMusic.value)
        val newindex = index.plus(1).mod(songsList.size)
        val newitem = songsList.get(newindex)

        currentMusic.update { newitem }

        mediaPlayer.setDataSource(newitem.media)
        mediaPlayer.prepareAsync()
        mediaPlayer.setOnPreparedListener{ mp ->
            mediaPlayer.start()
            maxduration.value = mp.duration.toFloat()
            startUpdatingProgress()
            sendNotification(currentMusic.value)
        }
    }

    fun prev(){
        mediaPlayer.reset()
        stopUpdatingProgress()
        mediaPlayer = MediaPlayer()

        val index = songsList.indexOf(currentMusic.value)


        val newindex = if(index > 0) index.minus(1) else 0
        val newitem = songsList.get(newindex)

        currentMusic.update { newitem }

        mediaPlayer.setDataSource(newitem.media)
        mediaPlayer.prepareAsync()
        mediaPlayer.setOnPreparedListener{ mp ->
            mediaPlayer.start()
            maxduration.value = mp.duration.toFloat()
            startUpdatingProgress()
            sendNotification(currentMusic.value)
        }
    }

    fun play_pause(){
        if(mediaPlayer.isPlaying){
            mediaPlayer.pause()
        } else{
            mediaPlayer.start()
            startUpdatingProgress()
        }
        sendNotification(currentMusic.value)
    }

    fun play(song : Song){

        stopUpdatingProgress()
        mediaPlayer.reset()
        mediaPlayer = MediaPlayer()
        mediaPlayer.setDataSource(song.media)
        mediaPlayer.prepareAsync()
        mediaPlayer.setOnPreparedListener{ mp ->
            mediaPlayer.start()
            maxduration.value = mp.duration.toFloat()
            startUpdatingProgress()
            sendNotification(song)
        }
    }


//    init {
//        // Update the currentDuration periodically using a handler or a coroutine
//        mediaPlayer.setOnPreparedListener { mp ->
//            maxduration.value = mp.duration.toFloat()
//            startUpdatingProgress()
//        }
//
//        mediaPlayer.setOnCompletionListener {
//            isplaying.value = false
//            currentDuration.value = 0f
//            stopUpdatingProgress()
//        }
//    }


    private fun sendNotification(song : Song){

        isplaying.update { mediaPlayer.isPlaying }

        val session = MediaSessionCompat(this , "music")


        val style  = androidx.media.app.NotificationCompat.MediaStyle()
            .setShowActionsInCompactView(0,1,2)
            .setMediaSession(session.sessionToken)

        val notification = NotificationCompat.Builder(this , channel_id)
            .setStyle(style)
            .setContentTitle(song.title)
            .setContentText(song.artist)
            .addAction( R.drawable.baseline_skip_previous_24 , "prev" , pendingintentprev())
            .addAction(if(mediaPlayer.isPlaying) R.drawable.baseline_pause_circle_outline_24 else R.drawable.baseline_play_circle_outline_24 , "play_pause" , pendingintentplay_pause())
            .addAction( R.drawable.baseline_skip_next_24 , "next" , pendingintentnext())
            .setSmallIcon(R.drawable.baseline_music_note_24)
            .setLargeIcon(BitmapFactory.decodeResource(resources ,R.drawable.music))
            .setSilent(true)
            .build()

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU){
            if(ContextCompat.checkSelfPermission(this , POST_NOTIFICATIONS ) == (PackageManager.PERMISSION_GRANTED)){
                startForeground(1,notification)
            }
        } else{
            startForeground(1,notification)
        }

    }


    fun pendingintentprev() : PendingIntent {
        val intent = Intent(this, MusicPlayerService ::class.java).apply {
            action = PREV
        }
        return PendingIntent.getService(
            this,
            0,
            intent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT )
    }

    fun pendingintentnext() : PendingIntent {
        val intent = Intent(this, MusicPlayerService ::class.java).apply {
            action = NEXT
        }
        return PendingIntent.getService(
            this,
            2,
            intent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT )
    }

    fun pendingintentplay_pause() : PendingIntent {
        val intent = Intent(this, MusicPlayerService ::class.java).apply {
            action = PLAY_PAUSE
        }
        return PendingIntent.getService(
            this,
            1,
            intent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT )
    }

    private var progressUpdateJob: Job? = null

    private fun startUpdatingProgress() {
        CoroutineScope(Dispatchers.IO).launch {
            while (mediaPlayer.isPlaying) {
                currentDuration.value = mediaPlayer.currentPosition.toFloat()
                Log.d("ProgressUpdate", "Current position: ${currentDuration.value}")
                delay(1000L) // Update every second
            }
            Log.d("ProgressUpdate", "Stopped updating progress")
        }
    }

    private fun stopUpdatingProgress() {
        currentDuration.value = 0f
        progressUpdateJob?.cancel()
        progressUpdateJob = null
    }

    override fun onTaskRemoved(rootIntent: Intent?) {
        super.onTaskRemoved(rootIntent)
        stopSelf()
    }


    override fun onDestroy() {
        mediaPlayer?.release()
        super.onDestroy()
    }


}