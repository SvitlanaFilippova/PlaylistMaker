package com.practicum.playlistmaker.domain.search

import com.practicum.playlistmaker.domain.Track

interface TrackInteractor {
    fun search(expression: String, consumer: TracksConsumer)



    interface TracksConsumer {
        fun consume(foundTracks: ArrayList<Track>?, errorMessage: String?)
    }
}