package com.playlistmaker.ui.search

import android.annotation.SuppressLint
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.EditText
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.gson.Gson
import com.playlistmaker.domain.Track
import com.playlistmaker.ui.search.view_model.SearchScreenState
import com.playlistmaker.ui.search.view_model.SearchViewModel
import com.playlistmaker.util.hideKeyBoard
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.databinding.FragmentSearchBinding
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel


class SearchFragment : Fragment() {
    private val viewModel by viewModel<SearchViewModel>()
    private var _binding: FragmentSearchBinding? = null
    private val binding: FragmentSearchBinding get() = requireNotNull(_binding) { "Binding wasn't initialized" }

    private val tracksAdapter: SearchAdapter by lazy {
        SearchAdapter { track ->
            if (clickDebounce()) {
                viewModel.updateHistory(track)
                showPlayer(track)
            }
        }
    }
    private var isClickAllowed = true

    private var searchInput: String = INPUT_DEF

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSearchBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        isClickAllowed = true
        viewModel.getSearchState().observe(viewLifecycleOwner)
        { state ->
            renderSearchScreen(state)
            hideProgressBar(state)
        }

        val inputEditText = binding.etInputSearch
        if (searchInput.isNotEmpty()) {
            inputEditText.setText(searchInput)
        }
        binding.searchRvResults.apply {
            layoutManager =
                LinearLayoutManager(requireActivity(), LinearLayoutManager.VERTICAL, false)
            adapter = tracksAdapter
        }
        setOnClickListeners()
        setEditTextListeners(inputEditText)
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

    private fun getTextWatcher(): TextWatcher {
        return object : TextWatcher {
            val inputEditText = binding.etInputSearch
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                binding.searchIvClearIcon.isVisible = !s.isNullOrEmpty()
                if (!s.isNullOrEmpty()) viewModel.searchWithDebounce(s.toString())

                if (inputEditText.hasFocus() && s?.isEmpty() == true) {
                    viewModel.setStateHistory()
                } else hideHistory()
            }

            override fun afterTextChanged(s: Editable?) {
                searchInput = s.toString()
                if (inputEditText.hasFocus() && s?.isEmpty() == true) {
                    viewModel.setStateHistory()
                } else hideHistory()
            }
        }
    }


    private fun cleanInput() {
        binding.etInputSearch.setText(INPUT_DEF)
        binding.etInputSearch.hideKeyBoard()
        viewModel.setStateHistory()
    }

    private fun placeholderManager(status: PlaceholderStatus) {
        with(binding) {
            when (status) {
                PlaceholderStatus.NOTHING_FOUND -> {
                    searchLlPlaceholder.isVisible = true
                    searchTvPlaceholderExtraMessage.isVisible = false
                    searchBvPlaceholderButton.isVisible = false
                    searchIvPlaceholderImage.apply {
                        setImageResource(R.drawable.ic_nothing_found)
                        isVisible = true
                    }
                    searchTvPlaceholderMessage.apply {
                        isVisible = true
                        text = getString(R.string.search_error_nothing_found)
                    }
                }

                PlaceholderStatus.NO_NETWORK -> {
                    searchLlPlaceholder.isVisible = true
                    searchBvPlaceholderButton.isVisible = true
                    searchIvPlaceholderImage.apply {
                        isVisible = true
                        setImageResource(R.drawable.ic_no_internet)
                    }
                    searchTvPlaceholderMessage.apply {
                        isVisible = true
                        text = context.getString(R.string.search_error_network)
                    }
                    searchTvPlaceholderExtraMessage.apply {
                        isVisible = true
                        text =
                            context.getString(R.string.search_error_network_extra)
                    }
                }

                PlaceholderStatus.HIDDEN -> {
                    searchLlPlaceholder.isVisible = false
                    searchIvPlaceholderImage.isVisible = false
                    searchTvPlaceholderMessage.isVisible = false
                    searchTvPlaceholderExtraMessage.isVisible = false
                    searchBvPlaceholderButton.isVisible = false
                }
            }
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun showHistory() {
        with(binding) {
            searchRvResults.isVisible = true
            searchBvClearHistory.isVisible = true
            searchTvSearchHistory.isVisible = true
            tracksAdapter.notifyDataSetChanged()
        }
    }

    private fun hideHistory() {
        with(binding) {
            searchRvResults.isVisible = false
            searchBvClearHistory.isVisible = false
            searchTvSearchHistory.isVisible = false
        }
    }

    private fun showSearchResults(foundTracks: ArrayList<Track>) {
        binding.searchRvResults.isVisible = true
        tracksAdapter.submitList(foundTracks)
    }

    private fun hideSearchResults(): Boolean {
        return false
    }

    private fun showProgressBar() {
        binding.searchProgressBar.isVisible = true
        hideHistory()
        hideSearchResults()
        placeholderManager(PlaceholderStatus.HIDDEN)
    }


    private fun hideProgressBar(state: SearchScreenState) {
        if (state != SearchScreenState.Loading) {
            binding.searchProgressBar.isVisible = false
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun renderSearchScreen(state: SearchScreenState) {
        when (state) {
            is SearchScreenState.Empty -> {
                hideHistory()
                hideSearchResults()
                placeholderManager(PlaceholderStatus.HIDDEN)
            }

            is SearchScreenState.Loading -> {
                showProgressBar()
            }

            is SearchScreenState.NetworkError -> {
                placeholderManager(PlaceholderStatus.NO_NETWORK)
            }

            is SearchScreenState.NothingFound -> {
                placeholderManager(PlaceholderStatus.NOTHING_FOUND)
            }

            is SearchScreenState.History -> {
                if (state.tracks.isNotEmpty()) {
                    showHistory()
                }
                with(tracksAdapter) {
                    submitList(state.tracks)
                    notifyDataSetChanged() // TODO заменить на инфо про конкретные изменения списка. Пока не знаю как.
                }
            }

            is SearchScreenState.SearchResults -> {
                showSearchResults(state.tracks)
                with(tracksAdapter) {
                    submitList(state.tracks)
                    notifyDataSetChanged()
                }
            }
        }
    }

    private fun showPlayer(track: Track) {
        val gson: Gson by inject()
        val trackJson = gson.toJson(track)
        findNavController().navigate(
            SearchFragmentDirections.actionSearchFragmentToPlayerFragment(trackJson)
        )
    }

    private fun setOnClickListeners() {
        with(binding) {
            searchIvClearIcon.setOnClickListener {
                cleanInput()
                viewModel.cancelSearchDebounce()
            }
            searchBvClearHistory.setOnClickListener {
                viewModel.clearHistory()


            }
        }
    }

    private fun setEditTextListeners(editText: EditText) {
        editText.apply {
            addTextChangedListener(getTextWatcher())
            setOnEditorActionListener { _, actionId, _ ->
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    if (text.isNotEmpty()) {
                        viewModel.cancelSearchDebounce()
                        viewModel.startSearch(text.toString())
                    }
                }
                false
            }
            binding.searchBvPlaceholderButton.setOnClickListener {
                viewModel.cancelSearchDebounce()
                viewModel.startSearch(text.toString())
                Log.d("DEBUG", "Тык на кнопку Обновить в плейсхолдере")
            }
        }

        editText.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus && editText.text.isEmpty()) {
                viewModel.setStateHistory()
            } else hideHistory()
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString(SEARCH_INPUT, searchInput)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private companion object {
        private const val SEARCH_INPUT = "SEARCH_INPUT"
        private const val INPUT_DEF = ""
        private const val CLICK_DEBOUNCE_DELAY = 1000L

        enum class PlaceholderStatus {
            NOTHING_FOUND,
            NO_NETWORK,
            HIDDEN
        }
    }
}

