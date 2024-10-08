package com.practicum.playlistmaker.domain.api

import com.practicum.playlistmaker.domain.models.Track
import com.practicum.playlistmaker.util.Resource

interface TracksRepository {

    val resultCode: Int

    fun searchTracks(expression: String): Resource<ArrayList<Track>>
}