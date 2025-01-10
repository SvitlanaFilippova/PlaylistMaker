package com.playlistmaker.ui.library.playlists

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.playlistmaker.domain.db.playlists.PlaylistsInteractor
import com.playlistmaker.domain.models.Playlist


import kotlinx.coroutines.launch

class NewPlaylistViewModel(private val playlistsInteractor: PlaylistsInteractor) : ViewModel() {

    private val stateLiveData = MutableLiveData<CreatingPlaylistState>()
    fun observeState(): LiveData<CreatingPlaylistState> = stateLiveData


    fun savePlaylist(playlist: Playlist) {
        stateLiveData.postValue(CreatingPlaylistState.Loading)
        viewModelScope.launch {
            playlistsInteractor.savePlaylist(playlist)
            stateLiveData.postValue(CreatingPlaylistState.Success(playlist.title))
        }
    }

    sealed interface CreatingPlaylistState {
        data object Loading : CreatingPlaylistState
        data class Success(
            val title: String
        ) : CreatingPlaylistState

    }

}