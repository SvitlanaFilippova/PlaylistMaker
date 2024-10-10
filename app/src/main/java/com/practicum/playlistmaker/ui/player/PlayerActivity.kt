package com.practicum.playlistmaker.ui.player

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.google.gson.Gson
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.databinding.ActivityPlayerBinding
import com.practicum.playlistmaker.domain.Track
import com.practicum.playlistmaker.ui.player.view_model.PlayerState
import com.practicum.playlistmaker.ui.player.view_model.PlayerViewModel

class PlayerActivity() : AppCompatActivity() {

    private lateinit var binding: ActivityPlayerBinding


    private val vm: PlayerViewModel by lazy {
        ViewModelProvider(
            this,
            PlayerViewModel.factory(track.previewUrl)
        )[PlayerViewModel::class.java]
    }

    val track: Track by lazy { Gson().fromJson(intent.getStringExtra("track"), Track::class.java) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPlayerBinding.inflate(layoutInflater)
        setContentView(binding.root)

        vm.getPlayerStateLiveData().observe(this) { playerState ->
            playbackControl(playerState)
        }

        binding.ibArrowBack.setOnClickListener {
            finish()
        }

        updateTrackData()
        binding.buttonPlay.setOnClickListener {
            togglePlaying()
        }
    }


    private fun updateTrackData() {
        binding.apply {
            tvTrackProgress.text = getString(R.string.default_track_progress)
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
        }
    }

    private fun playbackControl(playerState: PlayerState) {
        binding.apply {
            vm.getTrackProgressLiveData()
                .observe(this@PlayerActivity) { trackProgress ->
                    tvTrackProgress.text = trackProgress
                }
            when (playerState) {
                PlayerState.Prepared -> {
                    buttonPlay.isEnabled = true
                    buttonPlay.setImageResource(R.drawable.ic_play)
                }

                PlayerState.Default -> {
                    buttonPlay.isEnabled = false
                }

                PlayerState.Playing -> {
                    buttonPlay.setImageResource(R.drawable.ic_pause)
                }

                PlayerState.Paused -> {
                    buttonPlay.setImageResource(R.drawable.ic_play)
                }
            }
        }
    }

    private fun togglePlaying() {
        val playerState = vm.getPlayerStateLiveData().value
        when (playerState) {
            PlayerState.Playing -> {
                vm.pausePlayer()
            }

            else -> {
                vm.startPlayer()
            }
        }
    }

    override fun onPause() {
        super.onPause()
        vm.pausePlayer()
    }

}