package com.playlistmaker.data.db

import android.util.Log
import com.playlistmaker.data.db.entity.PlaylistEntity
import com.playlistmaker.data.toDomain
import com.playlistmaker.data.toEntity
import com.playlistmaker.domain.db.playlists.PlaylistsRepository
import com.playlistmaker.domain.models.Playlist
import com.playlistmaker.domain.models.Track
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class PlaylistsRepositoryImpl(private val appDatabase: AppDatabase) : PlaylistsRepository {
    override suspend fun savePlaylist(playlist: Playlist) {
        appDatabase.playlistDao().insertOrUpdatePlaylist(playlist.toEntity())
    }

    override fun getPlaylists(): Flow<List<Playlist>> = flow {
        val playlists = appDatabase.playlistDao().getAllPlaylists()
        emit(convertFromPlaylistEntity(playlists))
    }

    override suspend fun addToPlaylist(track: Track, playlist: Playlist) {
        val newPlaylist = playlist.copy(
            tracks = playlist.tracks + track.trackId.toString(),
            tracksQuantity = playlist.tracksQuantity + 1
        )

        val playlistEntity = newPlaylist.toEntity()

        with(appDatabase) {
            playlistDao().insertOrUpdatePlaylist(playlistEntity)
            newPlaylist.tracks.forEach { track -> Log.d("Debug", " $track") }

            trackDao().addTrack(track.toEntity(System.currentTimeMillis()))
        }
    }
}

private fun convertFromPlaylistEntity(playlists: List<PlaylistEntity>): List<Playlist> {
    return playlists.map { playlist -> playlist.toDomain() }
}
