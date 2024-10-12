package com.practicum.playlistmaker.domain.player


interface PlayerRepository {

    fun preparePlayer(trackUrl: String)
    fun setOnPreparedListener(onPrepare: (Boolean) -> Unit)
    fun setOnCompletionListener(onCompletion: (Boolean) -> Unit)
    fun startPlayer()

    fun pausePlayer()

    fun release()
    fun getCurrentPosition(): Int
}