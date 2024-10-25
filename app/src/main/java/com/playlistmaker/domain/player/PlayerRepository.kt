package com.playlistmaker.domain.player

import com.playlistmaker.domain.Track


interface PlayerRepository {

    fun preparePlayer(trackUrl: String)
    fun setOnPreparedListener(onPrepare: (Boolean) -> Unit)
    fun setOnCompletionListener(onCompletion: (Boolean) -> Unit)
    fun startPlayer()
    fun pausePlayer()
    fun release()

    fun getCurrentPosition(): Int

    fun addToFavorites(track: Track)
    fun removeFromFavorites(track: Track)
    fun checkIfTrackIsFavorite(track: Track): Boolean
}