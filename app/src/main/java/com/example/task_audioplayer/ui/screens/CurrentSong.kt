package com.example.task_audioplayer.ui.screens

import android.content.ComponentName
import android.content.Intent
import android.content.ServiceConnection
import android.os.Bundle
import android.os.IBinder
import android.util.Log
import android.widget.ImageView
import android.widget.SeekBar
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.task_audioplayer.MusicPlayerService
import com.example.task_audioplayer.R
import com.example.task_audioplayer.data.Song
import com.example.task_audioplayer.data.songsList
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class CurrentSong : AppCompatActivity() {

    private lateinit var ivAlbumArt: ImageView
    private lateinit var tvSongTitle: TextView
    private lateinit var tvArtistName: TextView
    private lateinit var seekBar: SeekBar
    private lateinit var tvStartTime: TextView
    private lateinit var tvEndTime: TextView
    private lateinit var btnPrevious: ImageView
    private lateinit var btnPlayPause: ImageView
    private lateinit var btnNext: ImageView


    private lateinit var song : Song

    private val currentMusic = MutableStateFlow<Song>(Song())
    private var musiclist = mutableListOf<Song>()
    private val maxduration = MutableStateFlow(0f)
    private val currentDuration = MutableStateFlow(0f)
    private val isplaying = MutableStateFlow<Boolean>(false)


    private var service : MusicPlayerService? = null
    private var isbounded = false


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_current_song)

        // Initialize Views
        ivAlbumArt = findViewById(R.id.ivAlbumArt)
        tvSongTitle = findViewById(R.id.tvSongTitle)
        tvArtistName = findViewById(R.id.tvArtistName)
        seekBar = findViewById(R.id.seekBar)
        tvStartTime = findViewById(R.id.tvStartTime)
        tvEndTime = findViewById(R.id.tvEndTime)
        btnPrevious = findViewById(R.id.btnPrevious)
        btnPlayPause = findViewById(R.id.btnPlayPause)
        btnNext = findViewById(R.id.btnNext)

        val btnBack = findViewById<ImageView>(R.id.btnBack)

        btnBack.setOnClickListener{ onBackPressed() }



        val songPosition = intent.getIntExtra("song", -1)

        val connection = object : ServiceConnection {
            override fun onServiceConnected(name: ComponentName?, binder: IBinder?) {
                service = (binder as MusicPlayerService.MusicBinder).getService()
                binder.setmusiclist(songsList)
//                binder.setcurrentMusic(songsList.get(songPosition))

                if(songPosition != -1){
                    service?.setCurrentMusic(songsList.get(songPosition))
                    loadSongData(songsList[songPosition])
                }

                lifecycleScope.launch {
                    binder.currentDuration().collectLatest {
//                        currentDuration.value = it
                        currentDuration.value = it
                        seekBar.progress = it.toInt()
                        tvStartTime.text = FormatTime(it.toFloat())
                    }
                }

                lifecycleScope.launch {
                    binder.isplaying().collectLatest {
                        isplaying.value = it
                        btnPlayPause.setImageResource(
                            if (it) R.drawable.baseline_pause_circle_outline_24 else R.drawable.baseline_play_circle_outline_24
                        )
                    }
                }

                lifecycleScope.launch {
                    binder.maxDuration().collectLatest {
                        maxduration.value = it
                        seekBar.max = it.toInt()
                        tvEndTime.text = FormatTime(it.toFloat())
                    }
                }

                lifecycleScope.launch {
                    binder.currentMusic().collectLatest {
                        currentMusic.value = it
                        loadSongData(it)
                    }
                }

                isbounded = true
            }

            override fun onServiceDisconnected(name: ComponentName?) {
                isbounded = false
            }

        }


        val intent = Intent(this@CurrentSong , MusicPlayerService::class.java)
        intent.putExtra("song" , songPosition)
        startService(intent)
        bindService(intent, connection, BIND_AUTO_CREATE)

        btnPlayPause.setOnClickListener {
            togglePlayPause()
        }

        // Handle Next Button
        btnNext.setOnClickListener {
            playNextSong()
        }

        // Handle Previous Button
        btnPrevious.setOnClickListener {
            playPreviousSong()
        }

        // SeekBar Listener
        seekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                if (fromUser) {
                    updatePlaybackPosition(progress)
                }
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {

            }
            override fun onStopTrackingTouch(seekBar: SeekBar?) {

            }
        })
    }

    fun FormatTime(duration: Float): String {
        val minutes = (duration / 60000).toInt()
        val seconds = ((duration % 60000) / 1000).toInt()
        return String.format("%d:%02d", minutes, seconds)
    }

    private fun loadSongData(song: Song) {
        // Assuming you have a Song list
//        song = songsList[position]

        // Load Data into Views
        tvSongTitle.text = song.title
        tvArtistName.text = song.artist

//        ivAlbumArt.setImageResource(song.albumArt) // Replace with actual album art resource

        // Dummy Example: Setting Total Duration
        tvStartTime.text = "0:00"
        tvEndTime.text = song.duration // Replace with actual duration from the song
    }

    private fun togglePlayPause() {
        if(isplaying.value) {
            service?.play_pause()
//            btnPlayPause.setImageResource(R.drawable.baseline_play_circle_outline_24)

        }
        else {
            service?.play_pause()
//            btnPlayPause.setImageResource(R.drawable.baseline_pause_circle_outline_24)
        }
//        btnPlayPause.setImageResource(R.drawable.baseline_pause_circle_outline_24) // Switch to play or pause icon
    }

    private fun playNextSong() {
        // Logic to play the next song
        service?.next()
        currentMusic.update { service?.currentMusic?.value ?: song }
        loadSongData( currentMusic.value)

    }

    private fun playPreviousSong() {
        // Logic to play the previous song
        service?.prev()
        currentMusic.update { service?.currentMusic?.value ?: song }
        loadSongData( currentMusic.value)

    }


    private fun updatePlaybackPosition(progress: Int) {
        // Logic to update playback position based on SeekBar

    }
}
