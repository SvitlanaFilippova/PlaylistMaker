package com.practicum.playlistmaker.domain.api


import android.content.SharedPreferences

interface ThemeRepository {


    val sharedPreferences: SharedPreferences

    fun save(isChecked: Boolean)
    fun read(): Boolean
}

