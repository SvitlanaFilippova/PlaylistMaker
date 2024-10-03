package com.practicum.playlistmaker.presentation.search

import com.practicum.playlistmaker.domain.models.Track

interface SearchResultsVisibilityManager {
    fun show(foundTracks: ArrayList<Track>): Boolean
    fun hide(): Boolean
}