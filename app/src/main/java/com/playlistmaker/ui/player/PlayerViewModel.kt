package com.playlistmaker.ui.player


import android.icu.text.SimpleDateFormat
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.playlistmaker.domain.StringProvider
import com.playlistmaker.domain.db.playlists.PlaylistsInteractor
import com.playlistmaker.domain.db.saved_tracks.SavedTracksInteractor
import com.playlistmaker.domain.models.Playlist
import com.playlistmaker.domain.models.Track
import com.playlistmaker.domain.player.PlayerInteractor
import com.playlistmaker.ui.library.playlists.PlaylistsState
import com.playlistmaker.ui.player.PlayerViewModel.Companion.DEFAULT_TRACK_PROGRESS
import com.practicum.playlistmaker.R
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.Locale

class PlayerViewModel(
    private var track: Track,
    private val playerInteractor: PlayerInteractor,
    private val savedTracksInteractor: SavedTracksInteractor,
    private val playlistsInteractor: PlaylistsInteractor,
    private val stringProvider: StringProvider
) : ViewModel() {

    private var playerState = MutableLiveData<PlayerState>(PlayerState.Default())
    fun getPlayerStateLiveData(): LiveData<PlayerState> {
        return playerState
    }

    private var isFavoriteLiveData = MutableLiveData<Boolean>()
    fun getIsFavoriteLiveData(): LiveData<Boolean> {
        return isFavoriteLiveData
    }

    private var playlistsLiveData = MutableLiveData<PlaylistsState>()
    fun getPlaylistsLiveData(): LiveData<PlaylistsState> {
        return playlistsLiveData
    }

    private var trackAddedLiveData = MutableLiveData<String>()
    fun getTrackAddedLiveData(): LiveData<String> {
        return trackAddedLiveData
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
        val wasTrackFavorited = track.inFavorite

        if (wasTrackFavorited != null) {
            val newTrack = track.copy(inFavorite = !wasTrackFavorited)
            viewModelScope.launch {
                savedTracksInteractor.changeFavorites(newTrack)
            }
            isFavoriteLiveData.value = !wasTrackFavorited

            this.track = newTrack
        }
    }


    fun checkIfFavorite(trackId: Int) {
        viewModelScope.launch {
            val isFavorite = savedTracksInteractor.checkIfTrackIsFavorite(trackId)
            isFavoriteLiveData.value = isFavorite
            track = track.copy(inFavorite = isFavorite)
        }
    }


    fun getPlaylists() {
        viewModelScope.launch {
            playlistsInteractor
                .getPlaylists()
                .collect { playlists ->
                    processResult(playlists)
                }
        }
    }

    private fun processResult(playlists: List<Playlist>) {
        if (playlists.isEmpty()) {
            playlistsLiveData.postValue(PlaylistsState.Empty)
            Log.d("DEBUG", "Список плейлистов пуст")
        } else {
            playlistsLiveData.postValue(PlaylistsState.Content(playlists))
        }
    }


    fun addTrackToPlaylist(playlist: Playlist) {
        val trackIds = playlist.tracks.map { it.trackId }
        if (trackIds.contains(track.trackId)) {
            trackAddedLiveData.value =
                stringProvider.getString(
                    R.string.track_is_already_added_to_playlist_template,
                    playlist.title
                )
        } else {
            viewModelScope.launch {
                playlistsInteractor.addToPlaylist(track, playlist)
                trackAddedLiveData.value =
                    stringProvider.getString(
                        R.string.success_added_to_playlist_template,
                        playlist.title
                    )
                getPlaylists()
            }

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


