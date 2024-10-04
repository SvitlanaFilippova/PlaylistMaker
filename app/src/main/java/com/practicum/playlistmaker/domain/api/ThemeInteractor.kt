package com.practicum.playlistmaker.domain.api


interface ThemeInteractor {


    fun read(): Boolean

    fun save(isChecked: Boolean)

}