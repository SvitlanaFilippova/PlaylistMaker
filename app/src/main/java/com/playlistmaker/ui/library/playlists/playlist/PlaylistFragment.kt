package com.playlistmaker.ui.library.playlists.playlist

import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.bumptech.glide.Glide
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.gson.Gson
import com.playlistmaker.domain.models.Playlist
import com.playlistmaker.domain.models.Track
import com.playlistmaker.ui.presentation.TrackAdapter
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.databinding.FragmentPlaylistBinding
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel

class PlaylistFragment : Fragment() {

    private var _binding: FragmentPlaylistBinding? = null
    private val binding: FragmentPlaylistBinding get() = requireNotNull(_binding) { "Binding wasn't initialized" }

    private var _playlist: Playlist? = null
    private val playlist: Playlist get() = requireNotNull(_playlist) { "Playlist wasn't initialized" }

    private var _adapter: TrackAdapter? = null
    private val adapter: TrackAdapter get() = requireNotNull(_adapter) { "TrackAdapter wasn't initialized" }

    private val gson: Gson by inject()
    private val args by navArgs<PlaylistFragmentArgs>()
    private val viewModel by viewModel<PlaylistViewModel>()

    private var trackListBSBehavior: BottomSheetBehavior<LinearLayout>? = null
    private var moreMenuBSBehavior: BottomSheetBehavior<LinearLayout>? = null
    private var descriprionIsExpanded = false


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPlaylistBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val jsonPlaylist = args.playlistJson
        _playlist = gson.fromJson(jsonPlaylist, Playlist::class.java)
        updatePlaylistData()

        val onTrackClickDebounce: (Track) -> Unit = { track ->
            showPlayer(track)
        }

        val onLongClickListener: (Track) -> Boolean = { track ->
            showDeleteConfirmationDialog(track.trackId)
            true
        }

        _adapter = TrackAdapter(onTrackClickDebounce, onLongClickListener)

        binding.apply {
            recyclerView.adapter = adapter
            trackListBSBehavior = BottomSheetBehavior.from(llBsTracksInPlaylist).apply {
                state = BottomSheetBehavior.STATE_COLLAPSED
            }

            moreMenuBSBehavior = BottomSheetBehavior.from(llBsMoreMenu).apply {
                state = BottomSheetBehavior.STATE_HIDDEN
            }


        }
        setCLickListeners()

        viewModel.getTracks(playlist.tracks)
        viewModel.getStateLiveData().observe(viewLifecycleOwner) {
            render(it)
        }
    }


    private fun updatePlaylistData() {
        binding.apply {
            with(playlist) {
                tvPlaylistName.text = title
                if (description != null) {
                    tvDescription.text = description
                } else tvDescription.isVisible = false


                Glide.with(requireContext())
                    .load(coverPath)
                    .centerCrop()
                    .placeholder(R.drawable.ic_big_placeholder)
                    .into(ivCover)

                with(smallPlaylistCard) {
                    Glide.with(requireContext())
                        .load(coverPath)
                        .centerCrop()
                        .placeholder(R.drawable.ic_big_placeholder)
                        .into(ivCover)

                    tvTitle.text = title
                }

            }
        }
    }

    private fun showDeleteConfirmationDialog(trackId: Int) {
        MaterialAlertDialogBuilder(requireActivity())
            .setTitle(getString(R.string.delete_track))
            .setMessage(getString(R.string.q_sure_u_want_to_delete_track))
            .setPositiveButton(getString(R.string.delete)) { _, _ ->
                viewModel.removeTrackFromPlaylist(trackId, playlist)
                removeTrackFromPlaylist(trackId)
                adapter.notifyDataSetChanged()
            }
            .setNegativeButton(getString(R.string.cancel), null)
            .show()

    }

    private fun removeTrackFromPlaylist(trackId: Int) {
        val newPlaylist = playlist.copy(
            tracks = playlist.tracks.filter { it != trackId },
            tracksQuantity = playlist.tracksQuantity - 1
        )
        this._playlist = newPlaylist
    }


    private fun render(state: PlaylistViewModel.TracksState) {
        when (state) {
            is PlaylistViewModel.TracksState.Content -> showContent(state.tracks)
            is PlaylistViewModel.TracksState.Empty -> showEmpty()
        }
    }

    private fun showEmpty() {
        with(binding) {
            recyclerView.visibility = View.GONE
            tvPlaceholderMessage.visibility = View.VISIBLE
            tvDuration.text = getString(R.string.zero_minutes)
            tvQuantity.text = viewModel.getQuantityText(0)
        }
    }

    private fun showContent(tracks: List<Track>) {
        val quantityText = viewModel.getQuantityText(tracks.size)
        with(binding) {
            recyclerView.visibility = View.VISIBLE
            tvPlaceholderMessage.visibility = View.GONE
            tvDuration.text = viewModel.getTotalDurationText(tracks)
            tvQuantity.text = quantityText
            smallPlaylistCard.tvQuantity.text = quantityText
            adapter.clearList()
            adapter.submitList(tracks as ArrayList<Track>)
            adapter.notifyDataSetChanged()
        }
    }

    private fun showPlayer(track: Track) {
        val gson: Gson by inject()
        val trackJson = gson.toJson(track)
        findNavController().navigate(
            PlaylistFragmentDirections.actionPlaylistToPlayerFragment(trackJson)
        )
    }

    private fun checkShareAction() {
        if (viewModel.getStateLiveData().value is PlaylistViewModel.TracksState.Empty)
            showMessageListIsEmpty()
        else sharePlaylist()
    }

    private fun sharePlaylist() {
        val state = viewModel.getStateLiveData().value
        val context = requireContext()
        if (state is PlaylistViewModel.TracksState.Content) {

            val message = buildString {
                appendLine(context.getString(R.string.playlist, playlist.title))
                playlist.description?.let { appendLine(it) }
                appendLine(viewModel.getQuantityText(playlist.tracksQuantity))
                appendLine() // Пустая строка перед списком треков

                state.tracks.forEachIndexed { index, track ->
                    appendLine("${index + 1}. ${track.artistName} - ${track.trackName} (${track.trackTime})")
                }
            }

            val intent = Intent(Intent.ACTION_SEND).apply {
                type = "text/plain"
                putExtra(Intent.EXTRA_TEXT, message)
            }

            context.startActivity(
                Intent.createChooser(
                    intent,
                    context.getString(R.string.share_playlist)
                )
            )
        }
    }


    private fun showMoreMenu() {
        moreMenuBSBehavior?.state = BottomSheetBehavior.STATE_HALF_EXPANDED
    }

    private fun setCLickListeners() {
        binding.apply {
            tvShare.setOnClickListener {
                sharePlaylist()
            }
            tvEdit.setOnClickListener {
                //TODO()
            }
            tvDeletePlaylist.setOnClickListener {
                //TODO()
            }

            ibArrowBack.setOnClickListener {
                findNavController().navigateUp()
            }

            ivShare.setOnClickListener {
                checkShareAction()
            }

            ivMore.setOnClickListener {
                showMoreMenu()
            }
            tvDescription.setOnClickListener {
                toggleExpandingDescription()
            }
        }
    }

    private fun showMessageListIsEmpty() {
        MaterialAlertDialogBuilder(requireActivity())
            .setMessage("В этом плейлисте нет списка треков, которым можно поделиться")
            .setNeutralButton("Ок, пойду добавлю", null)
            .show()
    }

    private fun toggleExpandingDescription() {
        binding.tvDescription.apply {
            if (descriprionIsExpanded) {
                maxLines = 1
                ellipsize = TextUtils.TruncateAt.END
            } else {
                maxLines = Int.MAX_VALUE
                ellipsize = null
            }
        }
        descriprionIsExpanded = !descriprionIsExpanded
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
        _playlist = null
        _adapter = null
    }


}