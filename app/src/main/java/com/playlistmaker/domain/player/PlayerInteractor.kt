package com.playlistmaker.domain.player

import com.playlistmaker.domain.Track

interface PlayerInteractor {
    fun prepare(trackUrl: String)
    fun setOnPreparedListener(onPrepare: (Boolean) -> Unit)
    fun setOnCompletionListener(
        onCompletion: (Boolean) -> Unit
    )

    fun start()
    fun pause()
    fun release()
    fun getCurrentPosition(): Int
    fun addToFavorites(track: Track)
    fun removeFromFavorites(track: Track)
    fun checkIfTrackIsFavorite(track: Track): Boolean
}


