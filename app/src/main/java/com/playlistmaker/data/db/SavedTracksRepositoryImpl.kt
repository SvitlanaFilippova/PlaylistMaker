package com.playlistmaker.data.db

import com.playlistmaker.data.db.entity.TrackEntity
import com.playlistmaker.data.toDomain
import com.playlistmaker.data.toEntity
import com.playlistmaker.domain.db.saved_tracks.SavedTracksRepository
import com.playlistmaker.domain.models.Track
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow


class SavedTracksRepositoryImpl(private val appDatabase: AppDatabase) :
    SavedTracksRepository {

    override suspend fun changeFavorites(track: Track) {
        appDatabase.trackDao().addTrack(track.toEntity(System.currentTimeMillis()))
    }

    override suspend fun checkIfTrackIsFavorite(trackId: Int): Boolean {
        return appDatabase.trackDao().checkIfTrackIsFavorite(trackId) != null
    }

    override fun getFavoriteTracks(): Flow<List<Track>> = flow {
        val tracks = appDatabase.trackDao().getFavoriteTracks()
        emit(convertFromTrackEntity(tracks))
    }

    private fun convertFromTrackEntity(tracks: List<TrackEntity>): List<Track> {
        return tracks.map { track -> track.toDomain() }
    }

    override fun getTracksByIds(ids: List<Int>): Flow<List<Track>> = flow {
        val tracks = appDatabase.trackDao().getTracksByIds(ids)
        emit(convertFromTrackEntity(tracks))
    }

}



