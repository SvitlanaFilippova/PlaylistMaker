package com.practicum.playlistmaker.domain.api

import com.practicum.playlistmaker.domain.models.Track

interface HistoryInteractor {

    fun clear()

    fun read(): List<Track>

    fun save(history: List<Track>)

}