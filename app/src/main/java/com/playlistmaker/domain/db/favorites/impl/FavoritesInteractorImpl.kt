package com.playlistmaker.domain.db.favorites.impl

import com.playlistmaker.domain.db.favorites.FavoritesInteractor
import com.playlistmaker.domain.db.favorites.FavoritesRepository
import com.playlistmaker.domain.models.Track
import kotlinx.coroutines.flow.Flow

class FavoritesInteractorImpl(private val repository: FavoritesRepository) : FavoritesInteractor {
    override suspend fun addToFavorites(track: Track) {
        repository.addToFavorites(track)
    }

    override suspend fun removeFromFavorites(trackId: Int) {
        repository.removeFromFavorites(trackId)
    }

    override suspend fun checkIfTrackIsFavorite(trackId: Int): Boolean {
        return repository.checkIfTrackIsFavorite(trackId)
    }

    override fun getFavoriteTracks(): Flow<List<Track>> {
        return repository.getFavoriteTracks()
    }
}