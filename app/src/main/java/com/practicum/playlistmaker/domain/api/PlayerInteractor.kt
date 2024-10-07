package com.practicum.playlistmaker.domain.api


interface PlayerInteractor {
    fun prepare()
    fun setOnPreparedListener(onPrepare: (buttonEnabled: Boolean, playerState: Int) -> Unit)
    fun setOnCompletionListener(
        onCompletion: (trackProgressText: String, image: Int, playerState: Int) -> Unit
    )

    fun playbackControl(
        playerState: Int,
        onStateChange: (imageRes: Int) -> Unit,
        onRun: (currentPosition: String) -> Unit
    ): Int

    fun pause(onPause: (imageRes: Int) -> Unit): Int
    fun stopRefreshingProgress()
    fun release()

}