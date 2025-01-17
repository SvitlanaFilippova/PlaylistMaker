package com.playlistmaker.ui.library.favorites

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.gson.Gson
import com.playlistmaker.domain.models.Track
import com.playlistmaker.ui.library.LibraryFragmentDirections
import com.playlistmaker.ui.presentation.TrackAdapter
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.databinding.FragmentFavoritesBinding
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel

class FavoritesFragment() : Fragment() {

    private var _binding: FragmentFavoritesBinding? = null
    private val binding: FragmentFavoritesBinding get() = requireNotNull(_binding) { "Binding wasn't initialized" }
    private var adapter: TrackAdapter? = null
    private val viewModel by viewModel<FavoritesViewModel>()
    private var isClickAllowed = true


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentFavoritesBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        isClickAllowed = true
        adapter = TrackAdapter(
            onTrackClickDebounce = { track ->
                if (clickDebounce()) {
                    showPlayer(track)
                }
            },
            onLongClickListener = { _ ->
                false
            }
        )
        with(binding) {
            rvFavoritesList.layoutManager =
                LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
            rvFavoritesList.adapter = adapter
        }
        viewModel.fillData()
        viewModel.observeState().observe(viewLifecycleOwner) {
            render(it)
        }
    }


    private fun render(state: FavoritesViewModel.FavoritesState) {
        when (state) {
            is FavoritesViewModel.FavoritesState.Content -> showContent(state.tracks)
            is FavoritesViewModel.FavoritesState.Empty -> showEmpty()
            is FavoritesViewModel.FavoritesState.Loading -> showLoading()
        }
    }

    private fun showLoading() {
        with(binding) {
            rvFavoritesList.visibility = View.GONE
            llPlaceholder.visibility = View.GONE
            progressBar.visibility = View.VISIBLE
        }
    }

    private fun showEmpty() {
        with(binding) {
            rvFavoritesList.visibility = View.GONE
            llPlaceholder.visibility = View.VISIBLE
            progressBar.visibility = View.GONE
            tvPlaceholderMessage.text = getString(R.string.your_library_is_empty)
        }
    }

    private fun showContent(tracks: List<Track>) {
        with(binding) {
            rvFavoritesList.visibility = View.VISIBLE
            llPlaceholder.visibility = View.GONE
            progressBar.visibility = View.GONE
            adapter?.clearList()
            adapter?.submitList(tracks as ArrayList<Track>)

        }
    }

    private fun clickDebounce(): Boolean {
        val current = isClickAllowed
        if (isClickAllowed) {
            isClickAllowed = false
            viewLifecycleOwner.lifecycleScope.launch {
                delay(CLICK_DEBOUNCE_DELAY)
                isClickAllowed = true
            }
        }
        return current
    }

    private fun showPlayer(track: Track) {
        val gson: Gson by inject()
        val trackJson = gson.toJson(track)
        findNavController().navigate(
            LibraryFragmentDirections.actionLibraryFragmentToPlayerFragment(trackJson)
        )
    }

    override fun onDestroyView() {
        super.onDestroyView()
        adapter = null
        binding.rvFavoritesList.adapter = null
        _binding = null
    }

    companion object {
        private const val CLICK_DEBOUNCE_DELAY = 1000L
        fun newInstance() = FavoritesFragment()
    }
}