package com.practicum.playlistmaker.domain.api

import com.practicum.playlistmaker.domain.models.Track

interface TrackInteractor {
    fun search(expression: String, consumer: TracksConsumer)



    interface TracksConsumer {
        fun consume(foundTracks: ArrayList<Track>?, errorMessage: String?)
    }
}