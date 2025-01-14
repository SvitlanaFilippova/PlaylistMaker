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
        appDatabase.playlistDao().savePlaylist(playlist.toEntity())
    }

    override fun getPlaylists(): Flow<List<Playlist>> = flow {
        val playlists = appDatabase.playlistDao().getPlaylists()
        emit(convertFromPlaylistEntity(playlists))
    }

    override suspend fun addToPlaylist(track: Track, playlist: Playlist) {
        val newPlaylist = playlist.copy(
            tracks = playlist.tracks + track.trackId.toString(),
            tracksQuantity = playlist.tracksQuantity + 1
        )
        // Репозиторий должен обновить список идентификаторов треков плейлиста,
        // увеличить счётчик количества треков
        appDatabase.playlistDao().savePlaylist(newPlaylist.toEntity())
        // и, используя соответствующий DAO-интерфейс,обновить запись изменённого плейлиста в базе данных.

        // Этот же репозиторий через DAO-интерфейс, работающий с таблицей треков добавляемых в плейлисты,
        // должен сохранить трек, переданный в репозиторий, в базу данных.
    }

    private fun convertFromPlaylistEntity(playlists: List<PlaylistEntity>): List<Playlist> {
        return playlists.map { playlist -> playlist.toDomain() }
    }
}