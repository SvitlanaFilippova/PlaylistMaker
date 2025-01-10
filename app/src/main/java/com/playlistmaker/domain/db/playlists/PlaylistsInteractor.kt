package com.playlistmaker.domain.db.playlists

import com.playlistmaker.domain.models.Playlist
import kotlinx.coroutines.flow.Flow

interface PlaylistsInteractor {
    suspend fun savePlaylist(playlist: Playlist)
    fun getPlaylists(): Flow<List<Playlist>>
}