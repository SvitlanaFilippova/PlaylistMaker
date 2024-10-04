package com.practicum.playlistmaker.data.repository

import android.icu.text.SimpleDateFormat
import android.media.MediaPlayer
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.databinding.ActivityPlayerBinding
import com.practicum.playlistmaker.domain.api.PlayerRepository
import com.practicum.playlistmaker.domain.models.Track
import com.practicum.playlistmaker.presentation.player.PlayerActivity.Companion.PROGRESS_REFRESH_DELAY_MILLIS
import com.practicum.playlistmaker.presentation.player.PlayerActivity.Companion.STATE_PAUSED
import com.practicum.playlistmaker.presentation.player.PlayerActivity.Companion.STATE_PLAYING
import com.practicum.playlistmaker.presentation.player.PlayerActivity.Companion.STATE_PREPARED
import java.util.Locale


class PlayerRepositoryImpl(
    val mediaPlayer: MediaPlayer,
    val binding: ActivityPlayerBinding,
    val mainThreadHandler: android.os.Handler?,
    val track: Track
) : PlayerRepository {

    val buttonPlay = binding.ibPlay


    override fun preparePlayer() {
        mediaPlayer.setDataSource(track.previewUrl)
        mediaPlayer.prepareAsync()
    }

    override fun setOnPreparedListener(): Int {
        buttonPlay.isEnabled = true
        return STATE_PREPARED
    }

    override fun setOnCompletionListener(): Int {
        buttonPlay.setImageResource(R.drawable.ic_play)
        stopRefreshingProgress()
        binding.tvTrackProgress.text = "00:00"
        return STATE_PREPARED
    }

    private fun startPlayer(): Int {
        mediaPlayer.start()
        buttonPlay.setImageResource(R.drawable.ic_pause)
        startRefreshingProgress()
        return STATE_PLAYING

    }

    override fun pausePlayer(): Int {
        mediaPlayer.pause()
        buttonPlay.setImageResource(R.drawable.ic_play)
        stopRefreshingProgress()
        return STATE_PAUSED
    }

    override fun playbackControl(playerState: Int): Int {
        when (playerState) {
            STATE_PLAYING -> {
                return pausePlayer()
            }

            STATE_PREPARED, STATE_PAUSED -> {
                return startPlayer()
            }

            else -> return 0
        }
    }

    private val refreshProgressRunnable = object : Runnable {
        override fun run() {
            binding.tvTrackProgress.text =
                SimpleDateFormat("mm:ss", Locale.getDefault()).format(mediaPlayer.currentPosition)

            mainThreadHandler?.postDelayed(
                this,
                PROGRESS_REFRESH_DELAY_MILLIS,
            )
        }
    }

    fun startRefreshingProgress() {
        mainThreadHandler?.postDelayed(
            refreshProgressRunnable, PROGRESS_REFRESH_DELAY_MILLIS
        )
    }

    override fun stopRefreshingProgress() {
        mainThreadHandler?.removeCallbacks(refreshProgressRunnable)
    }
}