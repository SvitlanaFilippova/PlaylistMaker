package com.playlistmaker.data.db

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
            tracks = playlist.tracks + track.trackId,
            tracksQuantity = playlist.tracksQuantity + 1
        )

        with(appDatabase) {
            playlistDao().insertOrUpdatePlaylist(newPlaylist.toEntity())
            trackDao().addTrack(track.toEntity(System.currentTimeMillis()))
        }
    }

    override suspend fun removeTrackFromPlaylist(trackId: Int, playlist: Playlist): List<Int> {
        val newTrackList = playlist.tracks.filter { it != trackId }
        val newPlaylist = playlist.copy(
            tracks = newTrackList,
            tracksQuantity = playlist.tracksQuantity - 1
        )

        with(appDatabase) {
            playlistDao().insertOrUpdatePlaylist(newPlaylist.toEntity())
            removeTrackFromDB(trackId)
        }
        return newTrackList
    }

    override suspend fun deletePlaylist(playlist: Playlist) {
        val trackList = playlist.tracks
        appDatabase.playlistDao().deletePlaylistById(playlist.id)
        trackList.forEach { trackId -> removeTrackFromDB(trackId) }
    }

    override suspend fun getPlaylistById(id: Int): Playlist {
        return appDatabase.playlistDao().getPlaylistById(id).toDomain()
    }


    private suspend fun removeTrackFromDB(trackId: Int) {
        with(appDatabase) {
            val wasTrackFavorite = appDatabase.trackDao().checkIfTrackIsFavorite(trackId) == null
            if (wasTrackFavorite) {
                getPlaylists().collect { playlists ->
                    val isTrackInAnyPlaylist =
                        playlists.any { playlist -> trackId in playlist.tracks }
                    if (!isTrackInAnyPlaylist) {
                        trackDao().deleteTracksById(trackId)
                    }
                }
            }
        }
    }

    private fun convertFromPlaylistEntity(playlists: List<PlaylistEntity>): List<Playlist> {
        return playlists.map { playlist -> playlist.toDomain() }
    }
}
