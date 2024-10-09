package com.practicum.playlistmaker.domain.search

import com.practicum.playlistmaker.domain.Track
import com.practicum.playlistmaker.util.Resource

interface TracksRepository {

        fun searchTracks(expression: String): Resource<ArrayList<Track>>

}