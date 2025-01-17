package com.playlistmaker.ui.library.playlists.new_playlist

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.playlistmaker.domain.StringProvider
import com.playlistmaker.domain.db.playlists.PlaylistsInteractor
import com.playlistmaker.domain.models.Playlist
import com.practicum.playlistmaker.R
import kotlinx.coroutines.launch

class NewPlaylistViewModel(
    private val playlistsInteractor: PlaylistsInteractor,
    private val stringProvider: StringProvider
) : ViewModel() {


    private val stateLiveData = MutableLiveData<CreatingPlaylistState>()
    fun getStateLiveData(): LiveData<CreatingPlaylistState> = stateLiveData


    fun savePlaylist(playlist: Playlist) {
        val currentState = stateLiveData.value
        stateLiveData.postValue(CreatingPlaylistState.Loading)
        var message: String? = null

        viewModelScope.launch {
            playlistsInteractor.savePlaylist(playlist)
            if (currentState is CreatingPlaylistState.NewPlaylistState) {
                message =
                    stringProvider.getString(R.string.playlist_created_success, playlist.title)
            } else if (currentState is CreatingPlaylistState.EditingPlaylist) {
                message = null
            }
            stateLiveData.postValue(CreatingPlaylistState.Success(message))
        }

    }

    fun checkIfSavedPlaylistExist(savedPlaylist: Playlist?) {
        if (savedPlaylist == null) stateLiveData.postValue(CreatingPlaylistState.NewPlaylistState)
        else stateLiveData.postValue(CreatingPlaylistState.EditingPlaylist(savedPlaylist))
    }

    sealed interface CreatingPlaylistState {
        data object Loading : CreatingPlaylistState
        data object NewPlaylistState : CreatingPlaylistState
        data class EditingPlaylist(
            val playlist: Playlist
        ) : CreatingPlaylistState

        data class Success(
            val message: String?
        ) : CreatingPlaylistState

    }

}