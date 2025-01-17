package com.playlistmaker.ui.library.playlists.playlist

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
import com.playlistmaker.ui.presentation.PlaylistAdapter.Companion.TREK_ENDING_A
import com.playlistmaker.ui.presentation.PlaylistAdapter.Companion.TREK_ENDING_OV
import com.practicum.playlistmaker.R
import kotlinx.coroutines.launch


class PlaylistViewModel(
    private val savedTracksInteractor: SavedTracksInteractor,
    private val playlistsInteractor: PlaylistsInteractor,
    private val stringProvider: StringProvider
) : ViewModel() {

    private val stateLiveData = MutableLiveData<TracksState>()
    fun getStateLiveData(): LiveData<TracksState> = stateLiveData


    fun getTracks(tracks: List<Int>) {
        viewModelScope.launch {
            savedTracksInteractor.getTracksByIds(tracks).collect { tracks ->
                processTracksRequestResult(tracks)
            }
        }
    }

    private fun processTracksRequestResult(tracks: List<Track>) {
        if (tracks.isEmpty()) {
            renderState(TracksState.Empty)
            Log.d("DEBUG", "Список треков пуст!")
        } else {
            renderState(TracksState.Content(tracks))
        }
    }

    private fun renderState(state: TracksState) {
        stateLiveData.value = state
    }


    fun removeTrackFromPlaylist(trackId: Int, playlist: Playlist) {
        viewModelScope.launch {
            val newTrackList = playlistsInteractor.removeTrackFromPlaylist(trackId, playlist)
            getTracks(newTrackList)
        }
    }


    fun getTotalDurationText(tracks: List<Track>): String {
        var totalDuration = 0
        tracks.forEach { track ->
            totalDuration += track.trackTimeMillis
        }
        totalDuration /= 60000

        val minutesEnding = when {
            totalDuration % 100 in 11..19 -> ""
            totalDuration % 10 == 1 -> MINUTE_ENDING_A
            totalDuration % 10 in 2..4 -> MINUTE_ENDING_I
            else -> ""
        }

        return stringProvider.getString(
            R.string.total_duration_template,
            totalDuration,
            minutesEnding
        )
    }

    fun getQuantityText(quantity: Int): String {
        return stringProvider.getString(
            R.string.quantity_of_tracks_template,
            quantity,
            getTracksEnding(quantity)
        )
    }

    private fun getTracksEnding(quantity: Int): String {
        val remainder10 = quantity % 10
        val remainder100 = quantity % 100
        val trackEnding =
            when {
                remainder100 in 11..19 -> TREK_ENDING_OV
                remainder10 == 1 -> ""
                remainder10 in 2..4 -> TREK_ENDING_A
                else -> TREK_ENDING_OV
            }
        return trackEnding
    }

    sealed interface TracksState {
        data class Content(
            val tracks: List<Track>
        ) : TracksState

        data object Empty : TracksState
    }

    companion object {
        const val MINUTE_ENDING_A = "а"
        const val MINUTE_ENDING_I = "ы"
    }
}


