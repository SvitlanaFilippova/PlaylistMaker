package com.practicum.playlistmaker.presentation.search

import com.practicum.playlistmaker.domain.models.Track

interface HistoryVisibilityManager {
    fun show(searchHistory: ArrayList<Track>)
    fun hide()
}