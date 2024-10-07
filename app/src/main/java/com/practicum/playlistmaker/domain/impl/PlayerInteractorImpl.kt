package com.practicum.playlistmaker.domain.impl

import com.practicum.playlistmaker.domain.api.PlayerInteractor
import com.practicum.playlistmaker.domain.api.PlayerRepository


class PlayerInteractorImpl(private val repository: PlayerRepository) : PlayerInteractor {
    override fun prepare(trackUrl: String) {
        repository.preparePlayer(trackUrl)
    }

    override fun setOnPreparedListener(onPrepare: (buttonEnabled: Boolean, playerState: Int) -> Unit) {
        return repository.setOnPreparedListener(onPrepare)
    }

    override fun setOnCompletionListener(
        onCompletion: (trackProgressText: String, image: Int, playerState: Int) -> Unit
    ) {
        return repository.setOnCompletionListener(onCompletion)
    }

    override fun playbackControl(
        playerState: Int,
        onStateChange: (imageRes: Int) -> Unit,
        onRun: (currentPosition: String) -> Unit
    ): Int {
        return repository.playbackControl(playerState, onStateChange, onRun)
    }

    override fun pause(onPause: (imageRes: Int) -> Unit): Int {
        return repository.pausePlayer(onPause)
    }

    override fun stopRefreshingProgress() {
        repository.stopRefreshingProgress()
    }

    override fun release() {
        repository.release()
    }

}

