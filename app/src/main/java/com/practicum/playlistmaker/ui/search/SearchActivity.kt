package com.practicum.playlistmaker.ui.search

import android.annotation.SuppressLint
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.text.TextWatcher
import android.view.inputmethod.EditorInfo
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.databinding.ActivitySearchBinding
import com.practicum.playlistmaker.domain.Track
import com.practicum.playlistmaker.ui.player.PlayerActivity
import com.practicum.playlistmaker.ui.search.view_model.SearchScreenState
import com.practicum.playlistmaker.ui.search.view_model.SearchViewModel
import com.practicum.playlistmaker.util.hideKeyBoard


class SearchActivity : AppCompatActivity() {
    private val vm: SearchViewModel by lazy {
        ViewModelProvider(this)[SearchViewModel::class.java]
    }

    private lateinit var binding: ActivitySearchBinding

    private val tracksAdapter: SearchAdapter by lazy {
        SearchAdapter(vm::onTrackClick)
    }
    private val mainThreadHandler: Handler by lazy { Handler(Looper.getMainLooper()) }
    private val searchRunnable: Runnable by lazy {
        Runnable { vm.startSearch(binding.etInputSearch.text.toString()) }
    }
    private var searchInput: String = INPUT_DEF

    @SuppressLint("NotifyDataSetChanged")
    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        binding = ActivitySearchBinding.inflate(layoutInflater)
        setContentView(binding.root)
        vm.getSearchState().observe(this@SearchActivity)
        { state -> //TODO сократить код, вынести отдельно часть во внешние функции
            renderSearchScreen(state)
            hideProgressBar(state)

            if (state is SearchScreenState.History) {
                with(tracksAdapter) {
                    submitList(state.tracks)
                    notifyDataSetChanged()
                }
            }
            if (state is SearchScreenState.SearchResults) {
                with(tracksAdapter) {
                    submitList(state.tracks)
                    notifyDataSetChanged()
                }
            }

        }
        vm.getPlayerTrigger().observe(this) { track: Track ->
            showPlayer(track)
        }

        val inputEditText = binding.etInputSearch

        if (searchInput.isNotEmpty()) {
            inputEditText.setText(searchInput)
        }
        setOnClickListeners()
        setEditTextListeners(inputEditText)


        binding.searchRvResults.apply {
            layoutManager = LinearLayoutManager(this@SearchActivity)
            adapter = tracksAdapter
        }

    }

    private fun getTextWatcher(): TextWatcher {
        return object : TextWatcher {
            val inputEditText = binding.etInputSearch
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                binding.searchIvClearIcon.isVisible = !s.isNullOrEmpty()
                if (s.isNullOrEmpty()) {
                    mainThreadHandler.removeCallbacks(searchRunnable)
                } else searchDebounce()

                if (inputEditText.hasFocus() && s?.isEmpty() == true) {
                    vm.setStateHistory()
                } else hideHistory()
            }

            override fun afterTextChanged(s: Editable?) {
                searchInput = s.toString()
                if (inputEditText.hasFocus() && s?.isEmpty() == true) {
                    vm.setStateHistory()
                } else hideHistory()
            }
        }
    }

    private fun searchDebounce() {
        mainThreadHandler.removeCallbacks(searchRunnable)
        mainThreadHandler.postDelayed(searchRunnable, SEARCH_DEBOUNCE_DELAY)
    }


    private fun cleanInput() {
        binding.etInputSearch.setText(INPUT_DEF)
        binding.etInputSearch.hideKeyBoard()
        mainThreadHandler.removeCallbacks(searchRunnable)
        vm.setStateHistory()
    }

    private fun placeholderManager(status: PlaceholderStatus) {
        binding.apply {
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
        binding.apply {
            searchRvResults.isVisible = true
            searchBvClearHistory.isVisible = true
            searchTvSearchHistory.isVisible = true
            tracksAdapter.notifyDataSetChanged()


        }
    }

    private fun hideHistory() {
        binding.apply {
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
            }

            is SearchScreenState.SearchResults -> {
                showSearchResults(state.tracks)
            }

        }
    }
    private fun showPlayer(track: Track) {
        PlayerActivity.show(this, track)
    }

    private fun setOnClickListeners() {
        binding.apply {
            searchIvClearIcon.setOnClickListener {
                cleanInput()
            }
            searchToolbar.setNavigationOnClickListener {
                finish()
            }
            searchBvClearHistory.setOnClickListener {
                vm.clearHistory()
            }
        }
    }

    private fun setEditTextListeners(editText: EditText) {
        binding.searchBvPlaceholderButton.setOnClickListener {
            vm.startSearch(editText.text.toString())
        }
        editText.addTextChangedListener(getTextWatcher())
        editText.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                if (editText.text.isNotEmpty()) {
                    mainThreadHandler.removeCallbacks(searchRunnable)
                    vm.startSearch(editText.text.toString())
                }
            }
            false
        }

        editText.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus && editText.text.isEmpty()) {
                vm.setStateHistory()
            } else hideHistory()

        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString(SEARCH_INPUT, searchInput)
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        searchInput = savedInstanceState.getString(SEARCH_INPUT, searchInput)
    }

    private companion object {
        private const val SEARCH_INPUT = "SEARCH_INPUT"
        private const val INPUT_DEF = ""
        private const val SEARCH_DEBOUNCE_DELAY = 2000L

        enum class PlaceholderStatus {
            NOTHING_FOUND,
            NO_NETWORK,
            HIDDEN
        }
    }
}

