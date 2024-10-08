package com.practicum.playlistmaker.domain.api

import com.practicum.playlistmaker.domain.models.Track

interface TracksSearchUseCase {
    fun execute(expression: String, consumer: TracksConsumer)

    interface TracksConsumer {
        fun consume(foundTracks: ArrayList<Track>)
        fun onError(resultCode: Int)
    }
}