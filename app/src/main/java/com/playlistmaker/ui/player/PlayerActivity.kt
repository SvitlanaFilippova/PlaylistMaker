package com.playlistmaker.ui.player

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.google.gson.Gson
import com.playlistmaker.domain.Track
import com.playlistmaker.ui.player.view_model.PlayerState
import com.playlistmaker.ui.player.view_model.PlayerViewModel
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.databinding.ActivityPlayerBinding
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf
import org.koin.java.KoinJavaComponent.getKoin

class PlayerActivity : AppCompatActivity() {

    private lateinit var binding: ActivityPlayerBinding
    private val gson: Gson by inject()
    val track: Track by lazy { gson.fromJson(intent.getStringExtra("track"), Track::class.java) }
    private val vm by viewModel<PlayerViewModel> { parametersOf(track.previewUrl) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPlayerBinding.inflate(layoutInflater)
        setContentView(binding.root)
        updateTrackData()

        vm.getPlayerStateLiveData().observe(this) { playerState ->
            playbackControl(playerState)
        }

        binding.ibArrowBack.setOnClickListener {
            finish()
        }

        binding.buttonPlay.setOnClickListener {
            togglePlaying()
        }
    }

    private fun updateTrackData() {
        try {
        binding.apply {
            tvTrackProgress.text = getString(R.string.default_track_progress)
            tvTrackName.text = track.trackName
            tvArtistName.text = track.artistName
            tvDurationTrack.text = track.trackTime
            tvYearTrack.text = track.releaseDate
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
        } catch (e: RuntimeException) {
            Log.e("DEBUG", track.toString())
        }
    }

    private fun playbackControl(playerState: PlayerState) {
        binding.apply {

            when (playerState) {
                PlayerState.Prepared -> {
                    buttonPlay.isEnabled = true
                    buttonPlay.setImageResource(R.drawable.ic_play)
                    updateTrackProgress(getString(R.string.default_track_progress))
                }

                PlayerState.Default -> {
                    buttonPlay.isEnabled = false
                }

                PlayerState.Error -> {
                    Toast.makeText(
                        this@PlayerActivity,
                        R.string.no_demo_for_this_track,
                        Toast.LENGTH_SHORT
                    ).show()
                }

                is PlayerState.Paused -> {
                    buttonPlay.setImageResource(R.drawable.ic_play)
                    val trackProgress =
                        (vm.getPlayerStateLiveData().value as PlayerState.Paused).trackProgressData
                    updateTrackProgress(trackProgress)
                }

                is PlayerState.Playing -> {
                    buttonPlay.setImageResource(R.drawable.ic_pause)
                    val trackProgress =
                        (vm.getPlayerStateLiveData().value as PlayerState.Playing).trackProgressData
                    updateTrackProgress(trackProgress)
                }
            }
        }
    }

    private fun updateTrackProgress(trackProgress: String) {
        binding.tvTrackProgress.text = trackProgress
    }

    private fun togglePlaying() {
        val playerState = vm.getPlayerStateLiveData().value
        when (playerState) {
            is PlayerState.Playing -> {
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


    companion object {
        private const val TRACK = "track"
        private val gson: Gson = getKoin().get()
        fun show(context: Context, track: Track) {
            val intent = Intent(
                Intent(context, PlayerActivity::class.java)
            ).apply {
                putExtra(TRACK, gson.toJson(track))
            }
            context.startActivity(intent)
        }
    }
}