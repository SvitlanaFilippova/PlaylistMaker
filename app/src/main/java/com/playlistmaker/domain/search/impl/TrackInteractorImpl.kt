package com.playlistmaker.domain.search.impl

import com.playlistmaker.domain.models.Track
import com.playlistmaker.domain.search.TrackInteractor
import com.playlistmaker.domain.search.TracksRepository
import com.playlistmaker.util.Resource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class TrackInteractorImpl(private val repository: TracksRepository) : TrackInteractor {

    override fun searchTracks(expression: String): Flow<Pair<ArrayList<Track>?, String?>> {
        return repository.searchTracks(expression).map { result ->
            when (result) {
                is Resource.Success -> {
                    Pair(result.data, null)
                }

                is Resource.Error -> {
                    Pair(null, result.message)
                }
            }
        }
    }
}





