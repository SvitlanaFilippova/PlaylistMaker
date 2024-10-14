package com.playlistmaker.data.search

data class TracksSearchResponse
    (
    val searchType: String,
    val expression: String,
    val results: ArrayList<TrackDto>
) : Response()

