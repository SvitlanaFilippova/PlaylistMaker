package com.playlistmaker.ui.library.playlists

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.playlistmaker.domain.models.Playlist
import com.playlistmaker.ui.library.LibraryFragmentDirections
import com.playlistmaker.ui.presentation.PlaylistAdapter

import com.practicum.playlistmaker.databinding.FragmentPlaylistsBinding
import org.koin.androidx.viewmodel.ext.android.viewModel

class PlaylistsFragment : Fragment() {
    private var _binding: FragmentPlaylistsBinding? = null
    private val binding: FragmentPlaylistsBinding get() = requireNotNull(_binding) { "Binding wasn't initialized" }
    private val viewModel by viewModel<PlaylistsViewModel>()
    private var adapter: PlaylistAdapter? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPlaylistsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.btCreatePlaylist.setOnClickListener {
            findNavController().navigate(
                LibraryFragmentDirections.actionLibraryFragmentToNewPlaylistFragment()
            )
        }
        adapter = PlaylistAdapter(
            viewType = PlaylistAdapter.MEDIUM_PLAYLISTS_GRID,
            onItemClick = { playlist ->
                // TODO
            })
        binding.recyclerView.adapter = adapter
        viewModel.fillData()
        viewModel.observeState().observe(viewLifecycleOwner) {
            render(it)
        }
    }


    private fun render(state: PlaylistsState) {
        when (state) {
            PlaylistsState.Loading -> showProgressBar()
            PlaylistsState.Empty -> showPlaceholder()
            is PlaylistsState.Content -> showContent(state.playlists)
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

    private fun showContent(playlists: List<Playlist>) {
        binding.apply {
            recyclerView.isVisible = true
            progressbar.isVisible = false
            llPlaceholder.isVisible = false
        }
        adapter?.clearList()
        adapter?.submitList(playlists as ArrayList)
        adapter?.notifyDataSetChanged()
    }


    override fun onDestroyView() {
        super.onDestroyView()
        adapter = null
        binding.recyclerView.adapter = null
        _binding = null

    }


    companion object {
        fun newInstance() = PlaylistsFragment()
    }
}