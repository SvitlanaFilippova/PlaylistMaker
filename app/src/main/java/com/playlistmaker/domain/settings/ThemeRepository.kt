package com.playlistmaker.domain.settings

interface ThemeRepository {


    fun save(isChecked: Boolean)
    fun read(): Boolean
    fun switchTheme(isChecked: Boolean)
    fun wasThemeSetManually(): Boolean
}

