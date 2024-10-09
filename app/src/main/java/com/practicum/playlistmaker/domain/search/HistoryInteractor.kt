package com.practicum.playlistmaker.domain.search

import com.practicum.playlistmaker.domain.Track

interface HistoryInteractor {

    fun clear()

    fun read(): List<Track>

    fun save(history: List<Track>)

}