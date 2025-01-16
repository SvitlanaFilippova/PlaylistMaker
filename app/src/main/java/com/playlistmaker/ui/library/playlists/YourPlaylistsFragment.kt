package com.playlistmaker.ui.library.playlists

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.google.gson.Gson
import com.playlistmaker.domain.models.Playlist
import com.playlistmaker.ui.library.LibraryFragmentDirections
import com.playlistmaker.ui.presentation.PlaylistAdapter
import com.practicum.playlistmaker.databinding.FragmentYourPlaylistsBinding
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel

class YourPlaylistsFragment : Fragment() {
    private var _binding: FragmentYourPlaylistsBinding? = null
    private val binding: FragmentYourPlaylistsBinding get() = requireNotNull(_binding) { "Binding wasn't initialized" }
    private val viewModel by viewModel<PlaylistsViewModel>()
    private var adapter: PlaylistAdapter? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentYourPlaylistsBinding.inflate(inflater, container, false)
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
                goToPlaylist(playlist)
            })
        binding.recyclerView.adapter = adapter
        viewModel.fillData()
        viewModel.observeState().observe(viewLifecycleOwner) {
            render(it)
        }
    }


    private fun render(state: PlaylistsState) {
        when (state) {
            PlaylistsState.Empty -> showPlaceholder()
            is PlaylistsState.Content -> showContent(state.playlists)
        }
    }


    private fun goToPlaylist(playlist: Playlist) {
        val gson: Gson by inject()
        val playlistJson = gson.toJson(playlist)
        findNavController().navigate(
            LibraryFragmentDirections.actionLibraryFragmentToPlaylist(playlistJson)
        )
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
        playlists.forEach { playlist ->
            Log.d(
                "DEBUG Your Playlists Fragment",
                "Плейлист = ${playlist.title}, tracksQuantity = ${playlist.tracksQuantity}, Треков в списке = ${playlist.tracks.size}, Треки: [${playlist.tracks}]"
            )
        }
        adapter?.clearList()
        adapter?.submitList(playlists as ArrayList)
        adapter?.notifyDataSetChanged()
    }

    override fun onResume() {
        super.onResume()
        viewModel.fillData()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        adapter = null
        binding.recyclerView.adapter = null
        _binding = null

    }


    companion object {
        fun newInstance() = YourPlaylistsFragment()
    }
}