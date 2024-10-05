package com.practicum.playlistmaker.presentation

import com.practicum.playlistmaker.domain.models.Track

interface HistoryUpdUseCase {
    fun upgrade(
        historyTrackList: ArrayList<Track>,
        historyIsVisible: Boolean,
        position: Int,
        adapter: TrackListAdapter
    )
}