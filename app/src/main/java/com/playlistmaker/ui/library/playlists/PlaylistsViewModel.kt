package com.playlistmaker.ui.library.playlists

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.playlistmaker.domain.db.playlists.PlaylistsInteractor
import com.playlistmaker.domain.models.Playlist

import kotlinx.coroutines.launch


class PlaylistsViewModel(private val playlistsInteractor: PlaylistsInteractor) : ViewModel() {

    private val stateLiveData = MutableLiveData<PlaylistsState>()
    fun observeState(): LiveData<PlaylistsState> = stateLiveData

    fun fillData() {
        stateLiveData.postValue(PlaylistsState.Loading)
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
            stateLiveData.postValue(PlaylistsState.Empty)
            Log.d("DEBUG", "Список плейлистов пуст")
        } else {
            stateLiveData.postValue(PlaylistsState.Content(playlists))
            Log.d("DEBUG", "Данные получены. $playlists")
        }
    }


}

sealed interface PlaylistsState {
    data object Empty : PlaylistsState
    data object Loading : PlaylistsState
    data class Content(
        val playlists: List<Playlist>
    ) : PlaylistsState

}