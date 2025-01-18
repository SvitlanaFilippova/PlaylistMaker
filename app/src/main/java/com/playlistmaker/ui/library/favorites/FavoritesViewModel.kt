package com.playlistmaker.ui.library.favorites


import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.playlistmaker.domain.db.saved_tracks.SavedTracksInteractor
import com.playlistmaker.domain.models.Track
import kotlinx.coroutines.launch


class FavoritesViewModel(
    private val savedTracksInteractor: SavedTracksInteractor
) : ViewModel() {
    private val stateLiveData = MutableLiveData<FavoritesState>()

    fun observeState(): LiveData<FavoritesState> = stateLiveData

    fun fillData() {
        renderState(FavoritesState.Loading)
        viewModelScope.launch {
            savedTracksInteractor
                .getFavoriteTracks()
                .collect { tracks ->
                    processResult(tracks)
                }
        }


    }

    private fun processResult(tracks: List<Track>) {
        if (tracks.isEmpty()) {
            renderState(FavoritesState.Empty)
            Log.d("DEBUG", "В избранных пусто")
        } else {
            renderState(FavoritesState.Content(tracks))
        }
    }


    private fun renderState(state: FavoritesState) {
        stateLiveData.postValue(state)
    }

    sealed interface FavoritesState {

        data object Loading : FavoritesState

        data class Content(
            val tracks: List<Track>
        ) : FavoritesState

        data object Empty : FavoritesState
    }
}