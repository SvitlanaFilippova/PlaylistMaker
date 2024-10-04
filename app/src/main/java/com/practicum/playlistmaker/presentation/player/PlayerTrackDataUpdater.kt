package com.practicum.playlistmaker.presentation.player

import com.practicum.playlistmaker.domain.models.Track

interface PlayerTrackDataUpdater {
    fun execute(track: Track) {

    }
}