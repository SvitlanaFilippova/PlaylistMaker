package com.practicum.playlistmaker.presentation

import android.icu.text.SimpleDateFormat
import android.media.MediaPlayer
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.ImageButton
import android.widget.TextView
import androidx.core.view.isVisible
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.google.gson.Gson
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.domain.models.Track
import com.practicum.playlistmaker.databinding.ActivityPlayerBinding
import java.util.Locale


class PlayerActivity() : AppCompatActivity() {
    private lateinit var binding: ActivityPlayerBinding
    private var mediaPlayer = MediaPlayer()
    private lateinit var buttonPlay: ImageButton
    private lateinit var url: String
    private lateinit var tvTrackProgress: TextView
    private var mainThreadHandler: Handler? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val track = Gson().fromJson(intent.getStringExtra("track"), Track::class.java)
        binding = ActivityPlayerBinding.inflate(layoutInflater)
        setContentView(binding.root)

        mainThreadHandler = Handler(Looper.getMainLooper())
        binding.apply {

            ibArrowBack.setOnClickListener() { finish() }

            buttonPlay = ibPlay
            buttonPlay.isEnabled = false
            url = track.previewUrl
            preparePlayer()
            tvTrackProgress.text = formatTime(0)
            tvTrackName.text = track.trackName
            tvArtistName.text = track.artistName
            tvDurationTrack.text = track.trackTime
            tvYearTrack.text = track.releaseDate.slice(0..3)

            tvGenreTrack.text = track.primaryGenreName
            tvCountryTrack.text = track.country
            Glide.with(applicationContext)
                .load(track.coverArtwork)
                .centerCrop()
                .transform(
                    RoundedCorners(
                        resources.getDimensionPixelSize(R.dimen.player_cover_radius_8)
                    )
                )
                .placeholder(R.drawable.ic_big_placeholder)
                .into(binding.ivCover)

            if (track.collectionName.isNotEmpty())
                tvCollectionTrack.text = track.collectionName
            else {
                tvCollectionTrack.isVisible = false
                tvCollectionTitle.isVisible = false
            }

            buttonPlay.setOnClickListener {
                playbackControl()

            }
        }


    }

    fun preparePlayer() {
        mediaPlayer.setDataSource(url)
        mediaPlayer.prepareAsync()
        mediaPlayer.setOnPreparedListener {
            buttonPlay.isEnabled = true
            playerState = STATE_PREPARED
        }
        mediaPlayer.setOnCompletionListener {
            buttonPlay.setImageResource(R.drawable.ic_play)
            stopRefreshingProgress()
            binding.tvTrackProgress.text = formatTime(0)
            playerState = STATE_PREPARED
        }
    }

    private fun startPlayer() {
        mediaPlayer.start()
        buttonPlay.setImageResource(R.drawable.ic_pause)
        playerState = STATE_PLAYING
        startRefreshingProgress()

    }

    private fun pausePlayer() {
        mediaPlayer.pause()
        buttonPlay.setImageResource(R.drawable.ic_play)
        playerState = STATE_PAUSED
        stopRefreshingProgress()
    }

    private fun playbackControl() {
        when (playerState) {
            STATE_PLAYING -> {
                pausePlayer()
            }

            STATE_PREPARED, STATE_PAUSED -> {
                startPlayer()
            }
        }
    }

    val refreshProgressRunnable = object : Runnable {
        override fun run() {
            binding.tvTrackProgress.text = formatTime(mediaPlayer.currentPosition)

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

    fun stopRefreshingProgress() {
        mainThreadHandler?.removeCallbacks(refreshProgressRunnable)
    }

    private fun formatTime(time: Int): String {
        return SimpleDateFormat("mm:ss", Locale.getDefault()).format(time)
    }

    override fun onPause() {
        super.onPause()
        pausePlayer()
    }

    override fun onDestroy() {
        super.onDestroy()
        stopRefreshingProgress()
        mediaPlayer.release()
    }

    companion object {
        private const val STATE_DEFAULT = 0
        private const val STATE_PREPARED = 1
        private const val STATE_PLAYING = 2
        private const val STATE_PAUSED = 3
        private const val PROGRESS_REFRESH_DELAY_MILLIS = 400L
    }

    private var playerState = STATE_DEFAULT
}