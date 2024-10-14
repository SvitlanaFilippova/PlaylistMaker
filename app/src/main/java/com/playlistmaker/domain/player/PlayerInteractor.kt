package com.playlistmaker.domain.player

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
}


//    fun playbackControl(
//        playerState: Int,
//        onStateChange: (imageRes: Int) -> Unit,
//        onRun: (currentPosition: String) -> Unit
//    ): Int