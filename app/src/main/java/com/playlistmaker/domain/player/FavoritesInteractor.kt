package com.playlistmaker.domain.player

import com.playlistmaker.domain.Track
import kotlinx.coroutines.flow.Flow

interface FavoritesInteractor {
    suspend fun addToFavorites(track: Track)
    suspend fun removeFromFavorites(track: Track)
    suspend fun checkIfTrackIsFavorite(trackId: Int): Boolean
    fun getFavoriteTracks(): Flow<List<Track>>

}