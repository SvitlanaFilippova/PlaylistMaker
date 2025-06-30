package com.playlistmaker.domain.search

import com.playlistmaker.data.util.Resource
import com.playlistmaker.domain.models.Track
import kotlinx.coroutines.flow.Flow

interface TracksRepository {

        fun searchTracks(expression: String): Flow<Resource<ArrayList<Track>>>

}