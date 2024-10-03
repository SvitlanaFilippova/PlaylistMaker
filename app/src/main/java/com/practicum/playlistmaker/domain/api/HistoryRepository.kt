package com.practicum.playlistmaker.domain.api


import android.content.SharedPreferences
import com.practicum.playlistmaker.domain.models.Track

interface HistoryRepository {


    val sharedPreferences: SharedPreferences

    fun save(history: ArrayList<Track>)
    fun read(): ArrayList<Track>
}

