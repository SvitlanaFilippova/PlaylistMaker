package com.playlistmaker.domain.db.saved_tracks

import com.playlistmaker.domain.models.Track
import com.playlistmaker.domain.models.TrackWithOrder
import kotlinx.coroutines.flow.Flow

interface SavedTracksInteractor {
    suspend fun changeFavorites(track: Track)
    suspend fun checkIfTrackIsFavorite(trackId: Int): Boolean
    fun getFavoriteTracks(): Flow<List<Track>>
    fun getTracksByIds(tracks: List<TrackWithOrder>): Flow<List<Track>>

}