package com.practicum.playlistmaker.data.player

import android.icu.text.SimpleDateFormat
import android.media.MediaPlayer
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.data.FavoritesStorage
import com.practicum.playlistmaker.domain.Track
import com.practicum.playlistmaker.domain.player.PlayerRepository
import com.practicum.playlistmaker.ui.player.PlayerActivity.Companion.PROGRESS_REFRESH_DELAY_MILLIS
import com.practicum.playlistmaker.ui.player.PlayerActivity.Companion.STATE_PAUSED
import com.practicum.playlistmaker.ui.player.PlayerActivity.Companion.STATE_PLAYING
import com.practicum.playlistmaker.ui.player.PlayerActivity.Companion.STATE_PREPARED
import java.util.Locale


class PlayerRepositoryImpl(
    private val mainThreadHandler: android.os.Handler,
    private val favoritesStorage: FavoritesStorage
) : PlayerRepository {

    val mediaPlayer = MediaPlayer()

    override fun preparePlayer(trackUrl: String) {
        mediaPlayer.setDataSource(trackUrl)
        mediaPlayer.prepareAsync()
    }


    override fun setOnPreparedListener(onPrepare: (buttonEnabled: Boolean, playerState: Int) -> Unit) {
        mediaPlayer.setOnPreparedListener {
            onPrepare(true, STATE_PREPARED)
        }
    }


    override fun setOnCompletionListener(
        onCompletion: (trackProgressText: String, image: Int, playerState: Int) -> Unit,
    ) {
        mediaPlayer.setOnCompletionListener {
            stopRefreshingProgress()
            onCompletion("00:00", R.drawable.ic_play, STATE_PREPARED)
        }

    }


    private fun startPlayer(
        onStart: (imageRes: Int) -> Unit,
        onRun: (currentPosition: String) -> Unit
    ): Int {
        mediaPlayer.start()
        onStart(R.drawable.ic_pause)
        startRefreshingProgress(onRun)
        return STATE_PLAYING

    }

    override fun pausePlayer(
        onPause: (imageRes: Int) -> Unit,
    ): Int {
        mediaPlayer.pause()
        onPause(R.drawable.ic_play)
        stopRefreshingProgress()
        return STATE_PAUSED
    }

    override fun playbackControl(
        playerState: Int,
        onStateChange: (imageRes: Int) -> Unit,
        onRun: (currentPosition: String) -> Unit
    ): Int {
        when (playerState) {
            STATE_PLAYING -> {
                return pausePlayer(onStateChange)
            }

            STATE_PREPARED, STATE_PAUSED -> {
                return startPlayer(onStateChange, onRun)
            }

            else -> return 0
        }
    }

    var refreshProgressRunnable = object : Runnable {
        override fun run() {
            postRunnableInMainThread(this)

        }
    }

    fun postRunnableInMainThread(r: Runnable) {
        mainThreadHandler.postDelayed(
            r,
            PROGRESS_REFRESH_DELAY_MILLIS
        )
    }

    fun getRefreshProgressRunnable(onRun: (currentPosition: String) -> Unit): Runnable {
        refreshProgressRunnable = object : Runnable {
            override fun run() {
                onRun(currentPositionToString())
                postRunnableInMainThread(this)
            }
        }
        return refreshProgressRunnable
    }

    private fun currentPositionToString(): String {
        return SimpleDateFormat("mm:ss", Locale.getDefault()).format(mediaPlayer.currentPosition)
    }

    private fun startRefreshingProgress(onRun: (currentPosition: String) -> Unit) {
        mainThreadHandler.postDelayed(
            getRefreshProgressRunnable(onRun), PROGRESS_REFRESH_DELAY_MILLIS
        )
    }

    override fun stopRefreshingProgress() {
        mainThreadHandler.removeCallbacks(refreshProgressRunnable)
    }

    override fun release() {
        mediaPlayer.release()
    }

    override fun addTrackToFavorites(track: Track) {
        favoritesStorage.addToFavorites(track.trackId)
    }

    override fun removeTrackFromFavorites(track: Track) {
        favoritesStorage.removeFromFavorites(track.trackId)
    }
}