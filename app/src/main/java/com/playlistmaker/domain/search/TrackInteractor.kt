package com.playlistmaker.domain.search

import com.playlistmaker.domain.Track
import kotlinx.coroutines.flow.Flow

interface TrackInteractor {
    fun searchTracks(expression: String): Flow<Pair<ArrayList<Track>?, String?>>

}