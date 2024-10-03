package com.practicum.playlistmaker.domain.api

import com.practicum.playlistmaker.domain.models.Track

interface HistoryInteractor {

    fun clear()

    fun read(): ArrayList<Track>

    fun save(history: ArrayList<Track>)

}