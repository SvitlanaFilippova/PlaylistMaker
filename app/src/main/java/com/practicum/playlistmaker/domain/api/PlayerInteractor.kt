package com.practicum.playlistmaker.domain.api


interface PlayerInteractor {
    fun prepare()
    fun setOnPreparedListener(): Int
    fun setOnCompletionListener(): Int
    fun playbackControl(playerState: Int): Int
    fun pause(): Int
    fun stopRefreshingProgress()

}