package com.playlistmaker.domain.db.playlists.impl

import com.playlistmaker.domain.db.playlists.PlaylistsInteractor
import com.playlistmaker.domain.db.playlists.PlaylistsRepository
import com.playlistmaker.domain.models.Playlist
import kotlinx.coroutines.flow.Flow

class PlaylistsInteractorImpl(private val repository: PlaylistsRepository) : PlaylistsInteractor {
    override suspend fun savePlaylist(playlist: Playlist) {
        repository.savePlaylist(playlist)
    }

    override fun getPlaylists(): Flow<List<Playlist>> {
        return repository.getPlaylists()
    }
}