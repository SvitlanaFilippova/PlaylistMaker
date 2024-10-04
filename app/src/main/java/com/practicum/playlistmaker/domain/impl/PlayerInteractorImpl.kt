package com.practicum.playlistmaker.domain.impl

import com.practicum.playlistmaker.domain.api.PlayerInteractor
import com.practicum.playlistmaker.domain.api.PlayerRepository


class PlayerInteractorImpl(val repository: PlayerRepository) : PlayerInteractor {
    override fun prepare() {
        repository.preparePlayer()
    }

    override fun setOnPreparedListener(): Int {
        return repository.setOnPreparedListener()
    }

    override fun setOnCompletionListener(): Int {
        return repository.setOnCompletionListener()
    }

    override fun playbackControl(playerState: Int): Int {
        return repository.playbackControl(playerState)
    }

    override fun pause(): Int {
        return repository.pausePlayer()
    }

    override fun stopRefreshingProgress() {
        repository.stopRefreshingProgress()
    }


}

