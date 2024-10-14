package com.playlistmaker.domain.player.impl

import com.playlistmaker.domain.player.PlayerInteractor
import com.playlistmaker.domain.player.PlayerRepository


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

