package com.practicum.playlistmaker.ui.player.view_model


import android.icu.text.SimpleDateFormat
import android.media.MediaPlayer
import android.os.Handler
import android.os.Looper
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.practicum.playlistmaker.domain.Track
import java.util.Locale

class PlayerViewModel(private val track: Track) : ViewModel() {

    private var mediaPlayer: MediaPlayer = MediaPlayer()
    private var playerStateLiveData = MutableLiveData<PlayerState>(PlayerState.Default)
    fun getPlayerStateLiveData(): LiveData<PlayerState> = playerStateLiveData
    private var trackProgressLiveData = MutableLiveData<String>(DEFAULT_TRACK_PROGRESS)
    fun getTrackProgressLiveData(): LiveData<String> = trackProgressLiveData
    private val mainThreadHandler: Handler by lazy { Handler(Looper.getMainLooper()) }

    init {
        setOnPreparedListener()
        preparePlayer()
        setOnCompletionListener()
    }

    private fun preparePlayer() {
        mediaPlayer.setDataSource(track.previewUrl)
        mediaPlayer.prepareAsync()
    }

    private fun setOnPreparedListener() {
        mediaPlayer.setOnPreparedListener {
            playerStateLiveData.value = PlayerState.Prepared
        }
    }

    private fun setOnCompletionListener() {
        mediaPlayer.setOnCompletionListener {
            playerStateLiveData.value = PlayerState.Prepared
            trackProgressLiveData.value = DEFAULT_TRACK_PROGRESS
            stopRefreshingProgress()
        }
    }


    fun startPlayer() {
        mediaPlayer.start()
        playerStateLiveData.value = PlayerState.Playing
        startRefreshingProgress()
    }

    fun pausePlayer() {
        mediaPlayer.pause()
        playerStateLiveData.value = PlayerState.Paused
        stopRefreshingProgress()
    }


    private fun currentPositionToString(): String {
        return SimpleDateFormat("mm:ss", Locale.getDefault()).format(mediaPlayer.currentPosition)
    }

    private val refreshProgressRunnable = object : Runnable {
        override fun run() {
            trackProgressLiveData.value = currentPositionToString()
            mainThreadHandler.postDelayed(

                this,
                PROGRESS_REFRESH_DELAY_MILLIS,
            )
        }
    }

    private fun startRefreshingProgress() {
        mainThreadHandler.postDelayed(
            refreshProgressRunnable, PROGRESS_REFRESH_DELAY_MILLIS
        )
    }

    private fun stopRefreshingProgress() {
        mainThreadHandler.removeCallbacks(refreshProgressRunnable)
    }

    override fun onCleared() {
        mediaPlayer.release()
        stopRefreshingProgress()
    }

    companion object {
        fun factory(track: Track): ViewModelProvider.Factory =
            viewModelFactory {
                initializer {
                    PlayerViewModel(track)
                }
            }

        const val DEFAULT_TRACK_PROGRESS = "00:00"
        const val PROGRESS_REFRESH_DELAY_MILLIS = 400L
    }
}
