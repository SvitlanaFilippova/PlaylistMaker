package com.playlistmaker.data.player

import android.media.MediaPlayer
import com.playlistmaker.domain.player.PlayerRepository


class PlayerRepositoryImpl(private var mediaPlayer: MediaPlayer) : PlayerRepository {


    override fun preparePlayer(trackUrl: String) {
        mediaPlayer.reset()
        mediaPlayer.setDataSource(trackUrl)
        mediaPlayer.prepareAsync()

    }

    override fun startPlayer() {
        mediaPlayer.start()
    }

    override fun pausePlayer() {
        mediaPlayer.pause()
    }

    override fun release() {
        mediaPlayer.release()
    }


    override fun getCurrentPosition(): Int {
        return mediaPlayer.currentPosition
    }


    override fun setOnPreparedListener(onPrepare: (Boolean) -> Unit) {
        mediaPlayer.setOnPreparedListener {
            onPrepare(true)
        }
    }

    override fun setOnCompletionListener(onCompletion: (Boolean) -> Unit) {
        mediaPlayer.setOnCompletionListener {
            onCompletion(true)
        }
    }
}