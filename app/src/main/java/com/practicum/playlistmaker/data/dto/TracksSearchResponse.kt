package com.practicum.playlistmaker.data.dto

import com.practicum.playlistmaker.domain.models.Track

data class TracksSearchResponse
    (
    val searchType: String,
    val expression: String,
    val results: ArrayList<TrackDto>
) : Response()

