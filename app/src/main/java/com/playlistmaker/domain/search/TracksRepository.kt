package com.playlistmaker.domain.search

import com.playlistmaker.domain.Track
import com.playlistmaker.util.Resource
import kotlinx.coroutines.flow.Flow

interface TracksRepository {

        fun searchTracks(expression: String): Flow<Resource<ArrayList<Track>>>

}