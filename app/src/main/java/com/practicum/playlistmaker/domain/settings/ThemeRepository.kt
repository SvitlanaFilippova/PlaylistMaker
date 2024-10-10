package com.practicum.playlistmaker.domain.settings

interface ThemeRepository {


    fun save(isChecked: Boolean)
    fun read(): Boolean
    fun switchTheme(isChecked: Boolean)
}

