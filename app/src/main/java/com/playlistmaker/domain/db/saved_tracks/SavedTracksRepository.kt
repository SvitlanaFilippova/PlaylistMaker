package com.playlistmaker.domain.db.saved_tracks

import com.playlistmaker.domain.models.Track
import kotlinx.coroutines.flow.Flow


interface SavedTracksRepository {

    suspend fun changeFavorites(track: Track)
    suspend fun checkIfTrackIsFavorite(trackId: Int): Boolean

    fun getFavoriteTracks(): Flow<List<Track>>

}