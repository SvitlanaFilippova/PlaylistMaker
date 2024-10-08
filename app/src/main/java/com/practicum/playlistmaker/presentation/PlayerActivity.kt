package com.practicum.playlistmaker.presentation

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.google.gson.Gson
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.databinding.ActivityPlayerBinding
import com.practicum.playlistmaker.domain.api.PlayerInteractor
import com.practicum.playlistmaker.domain.models.Track
import com.practicum.playlistmaker.util.Creator


class PlayerActivity() : AppCompatActivity() {

    private lateinit var binding: ActivityPlayerBinding
    private val mainThreadHandler: Handler by lazy { Handler(Looper.getMainLooper()) }
    private lateinit var playerInteractor: PlayerInteractor
    private var playerState = STATE_DEFAULT

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val track = Gson().fromJson(intent.getStringExtra("track"), Track::class.java)

        binding = ActivityPlayerBinding.inflate(layoutInflater)
        setContentView(binding.root)


        playerInteractor =
            Creator.providePlayerInteractor(mainThreadHandler)

        binding.ibArrowBack.setOnClickListener { finish() }
        binding.buttonPlay.isEnabled = false

        playerInteractor.prepare(track.previewUrl)

        playerInteractor.setOnPreparedListener { buttonEnabled, playerState ->
            binding.buttonPlay.isEnabled = buttonEnabled
            this.playerState = playerState
        }
        playerInteractor.setOnCompletionListener { trackProgressText, image, playerState ->
            binding.tvTrackProgress.text = trackProgressText
            binding.buttonPlay.setImageResource(image)
            this.playerState = playerState

        }

        binding.buttonPlay.setOnClickListener {
            playerState =
                playerInteractor.playbackControl(playerState,
                    { img -> binding.buttonPlay.setImageResource(img) },
                    { trackProgressText ->
                        binding.tvTrackProgress.text = trackProgressText
                    })

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

        playerState = playerInteractor.pause { img -> binding.buttonPlay.setImageResource(img) }
    }

    override fun onDestroy() {
        super.onDestroy()
        playerInteractor.stopRefreshingProgress()
        playerInteractor.release()

    }

    companion object {
        const val STATE_DEFAULT = 0
        const val STATE_PREPARED = 1
        const val STATE_PLAYING = 2
        const val STATE_PAUSED = 3
        const val PROGRESS_REFRESH_DELAY_MILLIS = 400L
    }


}