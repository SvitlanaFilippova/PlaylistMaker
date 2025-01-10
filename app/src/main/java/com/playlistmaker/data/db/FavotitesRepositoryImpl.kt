package com.playlistmaker.data.db

import com.playlistmaker.data.db.entity.TrackEntity
import com.playlistmaker.data.toDomain
import com.playlistmaker.data.toEntity
import com.playlistmaker.domain.db.favorites.FavoritesRepository
import com.playlistmaker.domain.models.Track
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow


class FavoritesRepositoryImpl(private val appDatabase: AppDatabase) :
    FavoritesRepository {

    override suspend fun addToFavorites(track: Track) {
        appDatabase.trackDao().addTrack(track.toEntity(System.currentTimeMillis()))
    }

    override suspend fun removeFromFavorites(trackId: Int) {
        appDatabase.trackDao().deleteTrackById(trackId)
    }

    override suspend fun checkIfTrackIsFavorite(trackId: Int): Boolean {
        return appDatabase.trackDao().getTrackById(trackId) != null
    }

    override suspend fun getFavoriteIds(): List<Int> {
        return appDatabase.trackDao().getFavoriteIds()
    }

    override fun getFavoriteTracks(): Flow<List<Track>> = flow {
        val tracks = appDatabase.trackDao().getTracks()
        emit(convertFromTrackEntity(tracks))
    }

    private fun convertFromTrackEntity(tracks: List<TrackEntity>): List<Track> {
        return tracks.map { track -> track.toDomain(inFavorite = true) }
    }

}



