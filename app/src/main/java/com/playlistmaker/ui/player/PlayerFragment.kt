package com.playlistmaker.ui.player


import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.gson.Gson
import com.playlistmaker.domain.models.Playlist
import com.playlistmaker.domain.models.Track
import com.playlistmaker.ui.library.playlists.PlaylistsState
import com.playlistmaker.ui.presentation.PlaylistAdapter
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
    private var adapter: PlaylistAdapter? = null
    private var bottomSheetBehavior: BottomSheetBehavior<LinearLayout>? = null

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

        adapter = PlaylistAdapter(
            viewType = PlaylistAdapter.SMALL_PLAYLISTS_LIST,
            onItemClick = { playlist ->
                viewModel.addTrackToPlaylist(playlist)
            })
        binding.recyclerView.adapter = adapter

        setBottomSheetBehavior()

        with(viewModel) {
            getPlaylists()

            getPlayerStateLiveData().observe(viewLifecycleOwner) { playerState ->
                playbackControl(playerState)
            }
            getIsFavoriteLiveData().observe(viewLifecycleOwner) { isFavorite ->
                toggleFavorite(isFavorite)
            }
            getPlaylistsLiveData().observe(viewLifecycleOwner) { state ->
                managePlaylists(state)
            }
            getTrackAddedLiveData().observe(viewLifecycleOwner) { message ->
                showMessage(message)
                bottomSheetBehavior?.state = BottomSheetBehavior.STATE_HIDDEN
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
            ibAddToPlaylist.setOnClickListener {
                bottomSheetBehavior?.state = BottomSheetBehavior.STATE_HALF_EXPANDED
            }
            btCreatePlaylist.setOnClickListener {
                findNavController().navigate(
                    PlayerFragmentDirections.actionPlayerFragmentToNewPlaylistFragment()
                )
            }
        }
    }

    private fun updateTrackData() {
        try {
            binding.apply {
                with(track) {
                    tvTrackProgress.text = getString(R.string.default_track_progress)
                    tvTrackName.text = trackName
                    tvArtistName.text = artistName
                    tvDurationTrack.text = trackTime
                    tvYearTrack.text = releaseDate
                    tvGenreTrack.text = primaryGenreName
                    tvCountryTrack.text = country

                    Glide.with(requireContext())
                        .load(coverArtwork)
                        .centerCrop()
                        .transform(
                            RoundedCorners(
                                resources.getDimensionPixelSize(R.dimen.player_cover_radius_8)
                            )
                        )
                        .placeholder(R.drawable.ic_big_placeholder)
                        .into(ivCover)

                    if (track.collectionName.isNotEmpty())
                        tvCollectionTrack.text = collectionName
                    else {
                        tvCollectionTrack.isVisible = false
                        tvCollectionTitle.isVisible = false
                    }
                    viewModel.checkIfFavorite(trackId)
                }
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
        binding.ibAddToFavorite.apply {
            if (isFavorite)
                setImageResource(R.drawable.ic_favorite_active)
            else
                setImageResource(R.drawable.ic_favorite_inactive)
        }
    }

    private fun managePlaylists(state: PlaylistsState) {
        when (state) {
            PlaylistsState.Loading -> showProgressBar()
            PlaylistsState.Empty -> showPlaceholder()
            is PlaylistsState.Content -> showPlaylists(state.playlists)
        }
    }

    private fun showProgressBar() {
        binding.apply {
            recyclerView.isVisible = false
            progressbar.isVisible = true
            llPlaceholder.isVisible = false
        }
    }

    private fun showPlaceholder() {
        binding.apply {
            recyclerView.isVisible = false
            progressbar.isVisible = false
            llPlaceholder.isVisible = true
        }
    }

    private fun showPlaylists(playlists: List<Playlist>) {
        binding.apply {
            recyclerView.isVisible = true
            progressbar.isVisible = false
            llPlaceholder.isVisible = false
        }
        adapter?.clearList()
        adapter?.submitList(playlists as ArrayList)
        adapter?.notifyDataSetChanged()
    }

    private fun showMessage(message: String) {
        Toast.makeText(requireActivity(), message, Toast.LENGTH_SHORT).show()
    }

    private fun setBottomSheetBehavior() {
        bottomSheetBehavior = BottomSheetBehavior.from(binding.llBsAddToPlaylist).apply {
            state = BottomSheetBehavior.STATE_HIDDEN
        }

        val overlay = binding.overlay

        bottomSheetBehavior?.addBottomSheetCallback(object :
            BottomSheetBehavior.BottomSheetCallback() {

            override fun onStateChanged(bottomSheet: View, newState: Int) {

                when (newState) {
                    BottomSheetBehavior.STATE_HIDDEN -> {
                        overlay.visibility = View.GONE
                    }

                    else -> {
                        overlay.visibility = View.VISIBLE
                    }
                }
            }

            override fun onSlide(bottomSheet: View, slideOffset: Float) {}
        })

    }

    override fun onPause() {
        super.onPause()
        viewModel.pausePlayer()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
        adapter = null
    }
}