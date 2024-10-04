package com.practicum.playlistmaker.domain.api


interface PlayerRepository {

    fun preparePlayer()
    fun setOnPreparedListener(): Int
    fun setOnCompletionListener(): Int
    fun playbackControl(playerState: Int): Int
    fun pausePlayer(): Int
    fun stopRefreshingProgress()
}
