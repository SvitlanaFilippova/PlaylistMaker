package com.practicum.playlistmaker.domain.settings


import android.content.SharedPreferences

interface ThemeRepository {


    val sharedPreferences: SharedPreferences

    fun save(isChecked: Boolean)
    fun read(): Boolean
    fun switchTheme(isChecked: Boolean)
}

