package com.practicum.playlistmaker.presentation

import com.practicum.playlistmaker.domain.models.Track
import com.practicum.playlistmaker.presentation.search.TrackListAdapter

interface HistoryUpdUseCase {
    fun upgrade(
        historyTrackList: ArrayList<Track>,
        historyIsVisible: Boolean,
        position: Int,
        adapter: TrackListAdapter
    )
}