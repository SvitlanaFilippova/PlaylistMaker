package com.playlistmaker.ui.library.playlists

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
        } else {
            stateLiveData.postValue(PlaylistsState.Content(playlists))
        }
    }


}

sealed interface PlaylistsState {
    data object Empty : PlaylistsState
    data class Content(
        val playlists: List<Playlist>
    ) : PlaylistsState

}