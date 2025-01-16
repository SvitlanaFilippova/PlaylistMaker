package com.playlistmaker.domain.db.playlists

import com.playlistmaker.domain.models.Playlist
import com.playlistmaker.domain.models.Track
import kotlinx.coroutines.flow.Flow

interface PlaylistsInteractor {
    suspend fun savePlaylist(playlist: Playlist)
    fun getPlaylists(): Flow<List<Playlist>>
    suspend fun addToPlaylist(track: Track, playlist: Playlist)
    suspend fun removeTrackFromPlaylist(trackId: Int, playlist: Playlist): List<Int>
}