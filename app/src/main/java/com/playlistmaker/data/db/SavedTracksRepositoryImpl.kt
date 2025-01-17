package com.playlistmaker.data.db

import android.util.Log
import com.playlistmaker.data.db.entity.TrackEntity
import com.playlistmaker.data.toDomain
import com.playlistmaker.data.toEntity
import com.playlistmaker.domain.db.saved_tracks.SavedTracksRepository
import com.playlistmaker.domain.models.Track
import com.playlistmaker.domain.models.TrackWithOrder
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

    override fun getTracksByIds(tracks: List<TrackWithOrder>): Flow<List<Track>> = flow {
        val sortedTracksWithOrder = tracks.sortedByDescending { it.timestamp }
        sortedTracksWithOrder.forEach { track ->
            Log.d(
                "DEBUG SavedTracksRep",
                "ДО выдачи из БД. Трек ${track.trackId}, время добавления: ${track.timestamp}"
            )
        }

        // Получаем только IDs отсортированных треков
        val trackIds = sortedTracksWithOrder.map { it.trackId }

        // Получаем треки из базы данных в том порядке, в котором они указаны в trackIds
        val tracksFromDb = appDatabase.trackDao().getTracksByIds(trackIds)

        // Создаём карту для быстрого поиска timestamp для каждого trackId
        val trackIdToTimestamp = sortedTracksWithOrder.associateBy { it.trackId }

        // Обновляем объекты Track, добавляя к ним timestamp из TrackWithOrder
        val tracksWithTimestamp = tracksFromDb.map { track ->
            val timestamp = trackIdToTimestamp[track.trackId]?.timestamp
            track.copy(timestamp = timestamp ?: 0L)  // Если timestamp отсутствует, ставим 0
        }
        // Сортируем треки по timestamp
        val finalSortedTracks = tracksWithTimestamp.sortedByDescending { it.timestamp }


        finalSortedTracks.forEach { track ->
            Log.d(
                "DEBUG SavedTracksRep",
                "Выдача из БД: Трек ${track.trackId}  ${track.trackName}"
            )
        }
        emit(convertFromTrackEntity(finalSortedTracks))
    }
}



