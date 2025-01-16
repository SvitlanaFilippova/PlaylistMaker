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
        // В репозитории редактируется список идентификаторов треков плейлиста и обновляются данные текущего плейлиста
        // в базе данных посредством соответствующего DAO-интерфейса (аналогично добавлению трека в плейлист).
        with(appDatabase) {
            playlistDao().insertOrUpdatePlaylist(newPlaylist.toEntity())

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
        return newTrackList
    }
}

private fun convertFromPlaylistEntity(playlists: List<PlaylistEntity>): List<Playlist> {
    return playlists.map { playlist -> playlist.toDomain() }
}
