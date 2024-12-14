package com.playlistmaker.domain.player.impl

import com.playlistmaker.domain.Track
import kotlinx.coroutines.flow.Flow


interface FavoritesRepository {

    suspend fun addToFavorites(track: Track)
    suspend fun removeFromFavorites(trackId: Int)
    suspend fun checkIfTrackIsFavorite(trackId: Int): Boolean
    suspend fun getFavoriteIds(): List<Int>
    fun getFavoriteTracks(): Flow<List<Track>>

}