package com.playlistmaker.domain.db.saved_tracks.impl

import com.playlistmaker.domain.db.saved_tracks.SavedTracksInteractor
import com.playlistmaker.domain.db.saved_tracks.SavedTracksRepository
import com.playlistmaker.domain.models.Track
import com.playlistmaker.domain.models.TrackWithOrder
import kotlinx.coroutines.flow.Flow

class SavedTracksInteractorImpl(private val repository: SavedTracksRepository) :
    SavedTracksInteractor {
    override suspend fun changeFavorites(track: Track) {
        repository.changeFavorites(track)
    }


    override suspend fun checkIfTrackIsFavorite(trackId: Int): Boolean {
        return repository.checkIfTrackIsFavorite(trackId)
    }

    override fun getFavoriteTracks(): Flow<List<Track>> {
        return repository.getFavoriteTracks()
    }

    override fun getTracksByIds(tracks: List<TrackWithOrder>): Flow<List<Track>> {
        return repository.getTracksByIds(tracks)
    }
}