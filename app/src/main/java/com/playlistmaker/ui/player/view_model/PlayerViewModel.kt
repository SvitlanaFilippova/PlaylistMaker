package com.playlistmaker.ui.player.view_model


import android.icu.text.SimpleDateFormat
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.playlistmaker.domain.Track
import com.playlistmaker.domain.player.FavoritesInteractor
import com.playlistmaker.domain.player.PlayerInteractor
import com.playlistmaker.ui.player.view_model.PlayerViewModel.Companion.DEFAULT_TRACK_PROGRESS
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.Locale

class PlayerViewModel(
    private var track: Track,
    private val playerInteractor: PlayerInteractor,
    private val favoritesInteractor: FavoritesInteractor
) : ViewModel() {

    private var playerState = MutableLiveData<PlayerState>(PlayerState.Default())
    fun getPlayerStateLiveData(): LiveData<PlayerState> {
        return playerState
    }

    private var isFavoriteLiveData = MutableLiveData<Boolean>()
    fun getIsFavoriteLiveData(): LiveData<Boolean> {
        return isFavoriteLiveData
    }

    private var timerJob: Job? = null


    init {
        setOnPreparedListener()
        preparePlayer()
        setOnCompletionListener()
    }

    private fun preparePlayer() {
        if (track.previewUrl.isNotEmpty())
            playerInteractor.prepare(track.previewUrl)
        else playerState.value = PlayerState.Error()
    }

    private fun setOnPreparedListener() {
        playerInteractor.setOnPreparedListener { isPrepared: Boolean ->
            if (isPrepared) {
                playerState.value = PlayerState.Prepared()
            }
        }
    }

    private fun setOnCompletionListener() {
        playerInteractor.setOnCompletionListener { isCompleted: Boolean ->
            if (isCompleted) {
                playerState.value = PlayerState.Prepared()
                timerJob?.cancel()
            }
        }
    }


    fun startPlayer() {
        if (track.previewUrl.isEmpty())
            playerState.value = PlayerState.Error()
        else {
            playerInteractor.start()
            playerState.value = PlayerState.Playing(getCurrentPlayerPosition())
            startTimer()
        }
    }


    fun pausePlayer() {
        playerInteractor.pause()
        timerJob?.cancel()
        playerState.value = PlayerState.Paused(getCurrentPlayerPosition())

    }


    private fun getCurrentPlayerPosition(): String {
        return SimpleDateFormat(
            "mm:ss",
            Locale.getDefault()
        ).format(playerInteractor.getCurrentPosition()) ?: "00:00"
    }


    private fun startTimer() {
        timerJob = viewModelScope.launch {
            while (true) {
                delay(PROGRESS_REFRESH_DELAY_MILLIS)
                playerState.postValue(PlayerState.Playing(getCurrentPlayerPosition()))
            }
        }
    }


    fun toggleFavorite() {
        viewModelScope.launch {
            if (track.inFavorite) {
                favoritesInteractor.removeFromFavorites(track)
            } else {
                favoritesInteractor.addToFavorites(track)
            }
        }
        isFavoriteLiveData.value = !track.inFavorite
        val newTrack = track.copy(inFavorite = !track.inFavorite)
        this.track = newTrack

    }

    fun checkIfFavorite(trackId: Int) {
        viewModelScope.launch {
            isFavoriteLiveData.value = favoritesInteractor.checkIfTrackIsFavorite(trackId)
        }
    }


    override fun onCleared() {
        playerInteractor.release()
        timerJob?.cancel()
    }

    companion object {
        const val DEFAULT_TRACK_PROGRESS = "00:00"
        const val PROGRESS_REFRESH_DELAY_MILLIS = 300L
    }
}

sealed class PlayerState(
    val isPlayButtonEnabled: Boolean,
    val button: PlayButtonAction,
    var progress: String
) {

    class Default : PlayerState(false, PlayButtonAction.PLAY, DEFAULT_TRACK_PROGRESS)
    class Prepared : PlayerState(true, PlayButtonAction.PLAY, DEFAULT_TRACK_PROGRESS)
    class Error : PlayerState(false, PlayButtonAction.PLAY, DEFAULT_TRACK_PROGRESS)
    data class Playing(val currentProgress: String) :
        PlayerState(true, PlayButtonAction.PAUSE, currentProgress)

    data class Paused(val currentProgress: String) :
        PlayerState(true, PlayButtonAction.PLAY, currentProgress)

    enum class PlayButtonAction {
        PLAY,
        PAUSE
    }
}
