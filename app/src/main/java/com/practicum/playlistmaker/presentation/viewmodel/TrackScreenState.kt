package com.practicum.playlistmaker.presentation.viewmodel

import com.practicum.playlistmaker.domain.models.Track

sealed class TrackScreenState {
    object Loading : TrackScreenState()

    data class Content(
        val trackModel: Track,
    ) : TrackScreenState()
}