package com.playlistmaker.ui.player


import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast


import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.google.gson.Gson
import com.playlistmaker.domain.Track
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.databinding.FragmentPlayerBinding
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf


class PlayerFragment : Fragment() {

    private var _binding: FragmentPlayerBinding? = null
    private val binding: FragmentPlayerBinding get() = requireNotNull(_binding) { "Binding wasn't initialized" }
    private val gson: Gson by inject()
    private val args by navArgs<PlayerFragmentArgs>()

    private lateinit var track: Track
    private val viewModel by viewModel<PlayerViewModel> { parametersOf(track) }


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPlayerBinding.inflate(inflater, container, false)
        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val jsonTrack = args.trackJson
        track = gson.fromJson(jsonTrack, Track::class.java)

        updateTrackData()
        with(viewModel) {
            getPlayerStateLiveData().observe(viewLifecycleOwner) { playerState ->
                playbackControl(playerState)
            }

            getIsFavoriteLiveData().observe(viewLifecycleOwner) { isFavorite ->
                toggleFavorite(isFavorite)
            }
        }

        with(binding) {
            ibArrowBack.setOnClickListener {
                findNavController().navigateUp()
            }

            buttonPlay.setOnClickListener {
                togglePlaying()
            }

            ibAddToFavorite.setOnClickListener {
                viewModel.toggleFavorite()
            }
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

                Glide.with(requireContext())
                    .load(track.coverArtwork)
                    .centerCrop()
                    .transform(
                        RoundedCorners(
                            resources.getDimensionPixelSize(R.dimen.player_cover_radius_8)
                        )
                    )
                    .placeholder(R.drawable.ic_big_placeholder)
                    .into(ivCover)

                if (track.collectionName.isNotEmpty())
                    tvCollectionTrack.text = track.collectionName
                else {
                    tvCollectionTrack.isVisible = false
                    tvCollectionTitle.isVisible = false
                }
                viewModel.checkIfFavorite(track.trackId)
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
                    requireContext(),
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

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}