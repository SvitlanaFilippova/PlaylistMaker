package com.playlistmaker.domain.settings


interface ThemeInteractor {


    fun read(): Boolean
    fun save(isChecked: Boolean)
    fun switchTheme(isChecked: Boolean)
    fun wasThemeSetManually(): Boolean
}