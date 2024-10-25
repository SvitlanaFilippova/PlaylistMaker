package com.playlistmaker.domain.player.impl

import com.playlistmaker.domain.Track
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

    override fun addToFavorites(track: Track) {
        repository.addToFavorites(track)
    }

    override fun removeFromFavorites(track: Track) {
        repository.removeFromFavorites(track)
    }

    override fun checkIfTrackIsFavorite(track: Track): Boolean {
        return repository.checkIfTrackIsFavorite(track)
    }
}

