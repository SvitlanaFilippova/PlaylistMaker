package com.practicum.playlistmaker.domain.settings


interface ThemeInteractor {


    fun read(): Boolean
    fun save(isChecked: Boolean)
    fun switchTheme(isChecked: Boolean)
}