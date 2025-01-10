package com.playlistmaker.domain.search


import com.playlistmaker.domain.models.Track

interface HistoryRepository {



    fun save(history: List<Track>)
    fun read(): List<Track>
    fun clear()
}

