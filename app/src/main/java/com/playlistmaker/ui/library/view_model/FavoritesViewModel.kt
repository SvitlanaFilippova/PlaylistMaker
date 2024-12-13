package com.playlistmaker.ui.library.view_model


import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.playlistmaker.domain.Track
import com.playlistmaker.domain.player.FavoritesInteractor
import kotlinx.coroutines.launch


class FavoritesViewModel(
    private val favoritesInteractor: FavoritesInteractor
) : ViewModel() {
    private val stateLiveData = MutableLiveData<FavoritesState>()

    fun observeState(): LiveData<FavoritesState> = stateLiveData

    fun fillData() {
        renderState(FavoritesState.Loading)
        viewModelScope.launch {
            favoritesInteractor
                .getFavoriteTracks()
                .collect { movies ->
                    processResult(movies)
                }
        }
    }

    private fun processResult(tracks: List<Track>) {
        if (tracks.isEmpty()) {
            renderState(FavoritesState.Empty)
        } else {
            renderState(FavoritesState.Content(tracks))
        }
    }

    private fun renderState(state: FavoritesState) {
        stateLiveData.postValue(state)
    }

    sealed interface FavoritesState {

        object Loading : FavoritesState

        data class Content(
            val tracks: List<Track>
        ) : FavoritesState

        object Empty : FavoritesState
    }
}