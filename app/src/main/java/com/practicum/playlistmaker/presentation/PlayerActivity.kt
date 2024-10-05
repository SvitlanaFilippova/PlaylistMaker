package com.practicum.playlistmaker.presentation

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.ImageButton
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.google.gson.Gson
import com.practicum.playlistmaker.Creator
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.databinding.ActivityPlayerBinding
import com.practicum.playlistmaker.domain.api.PlayerInteractor
import com.practicum.playlistmaker.domain.models.Track


class PlayerActivity() : AppCompatActivity() {
    private lateinit var binding: ActivityPlayerBinding
    private var mediaPlayer = Creator.provideMediaPlayer()
    private lateinit var buttonPlay: ImageButton
    private var mainThreadHandler: Handler? = null
    private lateinit var playerInteractor: PlayerInteractor


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val track = Gson().fromJson(intent.getStringExtra("track"), Track::class.java)
        binding = ActivityPlayerBinding.inflate(layoutInflater)
        setContentView(binding.root)

        mainThreadHandler = Handler(Looper.getMainLooper())
        playerInteractor =
            Creator.providePlayerInteractor(
                mediaPlayer,
                binding.ibPlay,
                binding.tvTrackProgress,
                mainThreadHandler,
                track
            )

        binding.ibArrowBack.setOnClickListener { finish() }

        binding.apply {
            buttonPlay = ibPlay
            buttonPlay.isEnabled = false
        }
        playerInteractor.prepare()

        mediaPlayer.setOnPreparedListener {
            playerState = playerInteractor.setOnPreparedListener()
        }

        mediaPlayer.setOnCompletionListener {
            playerState = playerInteractor.setOnCompletionListener()
        }

        buttonPlay.setOnClickListener {
            playerState = playerInteractor.playbackControl(playerState)

        }
        updateTrackData(track)
    }

    private fun updateTrackData(track: Track) {
        val context = applicationContext

        binding.apply {
            tvTrackProgress.text = context.getString(R.string.default_track_progress)
            tvTrackName.text = track.trackName
            tvArtistName.text = track.artistName
            tvDurationTrack.text = track.trackTime
            tvYearTrack.text = track.releaseDate.slice(0..3)
            tvGenreTrack.text = track.primaryGenreName
            tvCountryTrack.text = track.country

            Glide.with(context)
                .load(track.coverArtwork)
                .centerCrop()
                .transform(
                    RoundedCorners(
                        context.resources.getDimensionPixelSize(R.dimen.player_cover_radius_8)
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
        }
    }

    override fun onPause() {
        super.onPause()
        playerState = playerInteractor.pause()
    }

    override fun onDestroy() {
        super.onDestroy()
        playerInteractor.stopRefreshingProgress()
        mediaPlayer.release()
    }

    companion object {
        const val STATE_DEFAULT = 0
        const val STATE_PREPARED = 1
        const val STATE_PLAYING = 2
        const val STATE_PAUSED = 3
        const val PROGRESS_REFRESH_DELAY_MILLIS = 400L
    }

    private var playerState = STATE_DEFAULT
}