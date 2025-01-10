package com.playlistmaker.data.db

import com.playlistmaker.data.db.entity.PlaylistEntity
import com.playlistmaker.data.toDomain
import com.playlistmaker.data.toEntity
import com.playlistmaker.domain.db.playlists.PlaylistsRepository
import com.playlistmaker.domain.models.Playlist
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class PlaylistsRepositoryImpl(private val appDatabase: AppDatabase) : PlaylistsRepository {
    override suspend fun savePlaylist(playlist: Playlist) {
        appDatabase.playlistDao().savePlaylist(playlist.toEntity())
    }

    override fun getPlaylists(): Flow<List<Playlist>> = flow {
        val playlists = appDatabase.playlistDao().getPlaylists()
        emit(convertFromPlaylistEntity(playlists))
    }

    private fun convertFromPlaylistEntity(playlists: List<PlaylistEntity>): List<Playlist> {
        return playlists.map { playlist -> playlist.toDomain() }
    }
}