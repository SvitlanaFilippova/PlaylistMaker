package com.playlistmaker.domain.search

import com.playlistmaker.domain.models.Track

interface HistoryInteractor {

    fun clear()

    fun read(): List<Track>

    fun save(history: List<Track>)

}