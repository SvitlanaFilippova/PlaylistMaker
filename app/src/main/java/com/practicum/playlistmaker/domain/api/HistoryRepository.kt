package com.practicum.playlistmaker.domain.api


import android.content.SharedPreferences
import com.practicum.playlistmaker.domain.models.Track

interface HistoryRepository {


    val sharedPreferences: SharedPreferences

    fun save(history: List<Track>)
    fun read(): List<Track>
    fun clear()
}

