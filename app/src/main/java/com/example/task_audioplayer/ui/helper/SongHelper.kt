package com.example.task_audioplayer.ui.helper

import android.media.MediaPlayer
import kotlinx.coroutines.*

class SongHelper {
    companion object {
        private var mediaPlayer: MediaPlayer? = null
        private var currentPosition = 0
        private var job: Job? = null

        private var onProgressChanged: ((Float, Float) -> Unit)? = null

        fun playStream(url: String, progressCallback: ((Float, Float) -> Unit)? = null) {
            onProgressChanged = progressCallback

            if (mediaPlayer != null && currentPosition > 0) {
                mediaPlayer?.start()
                mediaPlayer?.seekTo(currentPosition)
                startTrackingProgress()
                return
            }

            mediaPlayer?.apply {
                try {
                    if (isPlaying) {
                        stop()
                    }
                    reset()
                } catch (e: IllegalStateException) {
                    e.printStackTrace()
                    release()
                }
            }

            mediaPlayer?.release()
            mediaPlayer = MediaPlayer().apply {
                setDataSource(url)
                prepareAsync()
            }

            mediaPlayer?.setOnPreparedListener { player ->
                player.seekTo(currentPosition)
                player.start()

                val duration = player.duration.toFloat()

                startTrackingProgress()
            }

            mediaPlayer?.setOnCompletionListener {
                releasePlayer()
            }
        }

        private fun startTrackingProgress() {
            job?.cancel()
            mediaPlayer?.let { player ->
                job = CoroutineScope(Dispatchers.Main).launch {
                    try {
                        val duration = player.duration.toFloat()
                        while (player.isPlaying) {
                            val position = player.currentPosition.toFloat()
                            onProgressChanged?.invoke(position, duration)
                            delay(500L)
                        }
                    } catch (e: IllegalStateException) {
                        e.printStackTrace()
                    }
                }
            }
        }

        fun pauseStream() {
            mediaPlayer?.let {
                try {
                    currentPosition = it.currentPosition
                    it.pause()
                } catch (e: IllegalStateException) {
                    e.printStackTrace()
                }
            }
        }

        fun stopStream() {
            mediaPlayer?.let {
                try {
                    if (it.isPlaying) {
                        it.stop()
                    }
                    it.reset()
                } catch (e: IllegalStateException) {
                    e.printStackTrace()
                }
            }
            currentPosition = 0
        }

        fun releasePlayer() {
            try {
                mediaPlayer?.apply {
                    stop()
                    reset()
                    release()
                }
            } catch (e: IllegalStateException) {
                e.printStackTrace()
            } finally {
                mediaPlayer = null
                currentPosition = 0
                job?.cancel()
            }
        }

        fun seekTo(position: Float) {
            try {
                mediaPlayer?.seekTo(position.toInt())
                currentPosition = position.toInt()
            } catch (e: IllegalStateException) {
                e.printStackTrace()
            }
        }
    }
}
