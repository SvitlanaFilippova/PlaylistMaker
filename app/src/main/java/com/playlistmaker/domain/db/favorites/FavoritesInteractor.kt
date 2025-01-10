package com.playlistmaker.domain.db.favorites

import com.playlistmaker.domain.models.Track
import kotlinx.coroutines.flow.Flow

interface FavoritesInteractor {
    suspend fun addToFavorites(track: Track)
    suspend fun removeFromFavorites(trackId: Int)
    suspend fun checkIfTrackIsFavorite(trackId: Int): Boolean
    fun getFavoriteTracks(): Flow<List<Track>>

}