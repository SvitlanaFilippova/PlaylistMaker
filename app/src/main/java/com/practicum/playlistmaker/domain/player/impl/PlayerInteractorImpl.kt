package com.practicum.playlistmaker.domain.player.impl

import com.practicum.playlistmaker.domain.player.PlayerInteractor
import com.practicum.playlistmaker.domain.player.PlayerRepository


class PlayerInteractorImpl(private val repository: PlayerRepository) : PlayerInteractor {
    override fun prepare(trackUrl: String) {
        repository.preparePlayer(trackUrl)
    }

    override fun setOnPreparedListener(
        onPrepare: (Boolean) -> Unit
    ) {
        return repository.setOnPreparedListener(onPrepare)
    }

    override fun setOnCompletionListener(
        onCompletion: (Boolean) -> Unit
    ) {
        return repository.setOnCompletionListener(onCompletion)
    }

    override fun start() {
        repository.startPlayer()
    }

    override fun pause() {
        repository.pausePlayer()
    }

    override fun release() {
        repository.release()
    }

    override fun getCurrentPosition(): Int {
        return repository.getCurrentPosition()
    }
}

//    override fun playbackControl(
//        playerState: Int,
//        onStateChange: (imageRes: Int) -> Unit,
//        onRun: (currentPosition: String) -> Unit
//    ): Int {
//        return repository.playbackControl(playerState, onStateChange, onRun)
//    }
