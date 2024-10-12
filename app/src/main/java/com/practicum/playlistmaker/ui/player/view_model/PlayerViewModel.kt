package com.practicum.playlistmaker.ui.player.view_model


import android.icu.text.SimpleDateFormat
import android.os.Handler
import android.os.Looper
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.practicum.playlistmaker.creator.Creator
import java.util.Locale

class PlayerViewModel(private val trackPreviewUrl: String) : ViewModel() {

    private var playerStateLiveData = MutableLiveData<PlayerState>(PlayerState.Default)
    fun getPlayerStateLiveData(): LiveData<PlayerState> = playerStateLiveData
    private val playerInteractor = Creator.providePlayerInteractor()
    private val mainThreadHandler: Handler by lazy { Handler(Looper.getMainLooper()) }

    init {
        setOnPreparedListener()
        preparePlayer()
        setOnCompletionListener()
    }

    private fun preparePlayer() {
        playerInteractor.prepare(trackPreviewUrl)
    }

    private fun setOnPreparedListener() {
        playerInteractor.setOnPreparedListener { isPrepared ->
            if (isPrepared) {
                playerStateLiveData.value = PlayerState.Prepared
            }
        }
    }

    private fun setOnCompletionListener() {
        playerInteractor.setOnCompletionListener { isCompleted ->
            if (isCompleted) {
                val currentState = playerStateLiveData.value
                if (currentState is PlayerState.Playing) {
                    (playerStateLiveData.value as PlayerState.Playing).trackProgressData =
                        DEFAULT_TRACK_PROGRESS
                }
                if (currentState is PlayerState.Paused) {
                    (playerStateLiveData.value as PlayerState.Paused).trackProgressData =
                        DEFAULT_TRACK_PROGRESS
                }

                playerStateLiveData.value = PlayerState.Prepared
                stopRefreshingProgress()
            }
        }
    }


    fun startPlayer() {
        playerInteractor.start()
        playerStateLiveData.value = PlayerState.Playing(currentPositionToString())
        startRefreshingProgress()
    }


    fun pausePlayer() {
        playerInteractor.pause()
        playerStateLiveData.value = PlayerState.Paused(currentPositionToString())
        stopRefreshingProgress()
    }


    private fun currentPositionToString(): String {
        return SimpleDateFormat(
            "mm:ss",
            Locale.getDefault()
        ).format(playerInteractor.getCurrentPosition())
    }

    private val refreshProgressRunnable = object : Runnable {
        override fun run() {
            refreshTrackProgress()
            mainThreadHandler.postDelayed(

                this,
                PROGRESS_REFRESH_DELAY_MILLIS,
            )
        }
    }

    private fun refreshTrackProgress() {
        val currentState = playerStateLiveData.value
        if (currentState is PlayerState.Playing) {
            val updatedState = currentState.copy(trackProgressData = currentPositionToString())
            playerStateLiveData.value = updatedState
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
        playerInteractor.release()
        stopRefreshingProgress()
    }

    companion object {
        fun factory(trackPreviewUrl: String): ViewModelProvider.Factory =
            viewModelFactory {
                initializer {
                    PlayerViewModel(trackPreviewUrl)
                }
            }

        const val DEFAULT_TRACK_PROGRESS = "00:00"
        const val PROGRESS_REFRESH_DELAY_MILLIS = 400L
    }
}

sealed interface PlayerState {
    data object Default : PlayerState
    data object Prepared : PlayerState
    data class Paused(var trackProgressData: String) : PlayerState
    data class Playing(var trackProgressData: String) : PlayerState
}