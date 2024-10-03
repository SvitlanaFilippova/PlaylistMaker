package com.practicum.playlistmaker.presentation.interfaces

import com.practicum.playlistmaker.domain.models.Track

interface HistoryVisibilityManager {
    fun show(searchHistory: ArrayList<Track>)
    fun hide()
}