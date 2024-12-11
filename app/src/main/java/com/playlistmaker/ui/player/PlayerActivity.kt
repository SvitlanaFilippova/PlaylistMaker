package com.playlistmaker.ui.player


import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.bundle.bundleOf
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

    private var _binding: ActivityPlayerBinding? = null
    private val binding: ActivityPlayerBinding get() = requireNotNull(_binding) { "Binding wasn't initialized" }
    private val gson: Gson by inject()
    private lateinit var track: Track
    private val viewModel by viewModel<PlayerViewModel> { parametersOf(track) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val jsonTrack = intent.extras?.getString(TRACK)
        if (jsonTrack != null) {
            track = gson.fromJson(jsonTrack, Track::class.java)
        }

        _binding = ActivityPlayerBinding.inflate(layoutInflater)
        setContentView(binding.root)
        updateTrackData()

        viewModel.getPlayerStateLiveData().observe(this) { playerState ->
            playbackControl(playerState)
        }

        viewModel.getIsFavoriteLiveData().observe(this) { isFavorite ->
            toggleFavorite(isFavorite)
        }

        binding.ibArrowBack.setOnClickListener {
            finish()
            Log.d("DEBUG", "Завершаю работу PlayerActivity")
        }

        binding.buttonPlay.setOnClickListener {
            togglePlaying()
        }

        binding.ibAddToFavorite.setOnClickListener {
            viewModel.toggleFavorite()
        }
    }

    private fun updateTrackData() {
        try {
            with(binding) {
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
                viewModel.checkIfFavorite(track)
            }
        } catch (e: RuntimeException) {
            Log.e("DEBUG", "Ошибка при попытке загрузить данные трека: $track")
        }
    }

    private fun playbackControl(playerState: PlayerState) {
        with(binding) {
            buttonPlay.isEnabled = playerState.isPlayButtonEnabled

            if (playerState is PlayerState.Error) {
                Toast.makeText(
                    this@PlayerActivity,
                    R.string.no_demo_for_this_track,
                    Toast.LENGTH_SHORT
                ).show()
                return
            }

            updateTrackProgress(playerState.progress)

            val image = when (playerState.button) {
                PlayerState.PlayButtonAction.PLAY -> R.drawable.ic_play
                else -> (R.drawable.ic_pause)
            }
            buttonPlay.setImageResource(image)

        }
    }

    private fun updateTrackProgress(trackProgress: String) {
        binding.tvTrackProgress.text = trackProgress
    }

    private fun togglePlaying() {
        val playerState = viewModel.getPlayerStateLiveData().value
        when (playerState) {
            is PlayerState.Playing -> {
                viewModel.pausePlayer()
            }

            else -> {
                viewModel.startPlayer()
            }
        }
    }

    private fun toggleFavorite(isFavorite: Boolean) {
        if (isFavorite)
            binding.ibAddToFavorite.setImageResource(R.drawable.ic_favorite_active)
        else
            binding.ibAddToFavorite.setImageResource(R.drawable.ic_favorite_inactive)
    }

    override fun onPause() {
        super.onPause()
        viewModel.pausePlayer()
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    companion object {
        const val TRACK = "track"
        private val gson: Gson = getKoin().get()
        fun createArgs(track: Track): Bundle =
            bundleOf(TRACK to gson.toJson(track))
    }

}