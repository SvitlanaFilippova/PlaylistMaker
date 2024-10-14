package com.playlistmaker.domain.search

import com.playlistmaker.domain.Track
import com.practicum.playlistmaker.util.Resource

interface TracksRepository {

        fun searchTracks(expression: String): Resource<ArrayList<Track>>

}