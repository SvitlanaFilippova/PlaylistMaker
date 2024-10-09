package com.practicum.playlistmaker.domain.search


import com.practicum.playlistmaker.domain.Track

interface HistoryRepository {



    fun save(history: List<Track>)
    fun read(): List<Track>
    fun clear()
}

