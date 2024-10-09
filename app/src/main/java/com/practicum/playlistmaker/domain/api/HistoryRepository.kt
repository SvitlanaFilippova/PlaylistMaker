package com.practicum.playlistmaker.domain.api


import com.practicum.playlistmaker.domain.models.Track

interface HistoryRepository {



    fun save(history: List<Track>)
    fun read(): List<Track>
    fun clear()
}

