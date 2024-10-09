package com.practicum.playlistmaker.ui.search

import android.annotation.SuppressLint
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.text.TextWatcher
import android.view.inputmethod.EditorInfo
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.creator.Creator
import com.practicum.playlistmaker.databinding.ActivitySearchBinding
import com.practicum.playlistmaker.domain.Track
import com.practicum.playlistmaker.domain.search.HistoryInteractor
import com.practicum.playlistmaker.domain.search.TrackInteractor
import com.practicum.playlistmaker.util.hideKeyBoard


class SearchActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySearchBinding
    private lateinit var historyInteractor: HistoryInteractor
    private val tracksAdapter: TrackListAdapter by lazy {
        TrackListAdapter(historyInteractor)
    }
    private val mainThreadHandler: Handler by lazy { Handler(Looper.getMainLooper()) }
    private val searchRunnable: Runnable by lazy {
        Runnable { startSearch(binding.etInputSearch.text.toString()) }
    }
    private var foundTracks = ArrayList<Track>()
    private var searchResultsIsVisible = false
    private var placeholderIsVisible = false
    private var searchInput: String = INPUT_DEF

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySearchBinding.inflate(layoutInflater)
        setContentView(binding.root)
        historyInteractor = Creator.provideHistoryInteractor()

        val searchClearButton = binding.searchIvClearIcon
        val inputEditText = binding.etInputSearch


        if (searchInput.isNotEmpty()) {
            inputEditText.setText(searchInput)
        }

        searchClearButton.setOnClickListener {
            cleanInput()
        }

        binding.searchToolbar.setNavigationOnClickListener {
            finish()
        }

        val placeholderUpdateButton = binding.searchBvPlaceholderButton
        placeholderUpdateButton.setOnClickListener {
            startSearch(inputEditText.text.toString())
        }


        inputEditText.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                if (inputEditText.text.isNotEmpty()) {
                    mainThreadHandler.removeCallbacks(searchRunnable)
                    startSearch(inputEditText.text.toString())
                }
            }
            false
        }


        inputEditText.setOnFocusChangeListener { _, hasFocus ->
            if (!searchResultsIsVisible) {
                if (hasFocus && inputEditText.text.isEmpty()) {
                    showHistory(historyInteractor.read() as ArrayList<Track>)
                } else hideHistory()
            }

        }


        val searchTextWatcher = getTextWatcher()

        inputEditText.addTextChangedListener(searchTextWatcher)

        val tracksRecyclerView = binding.searchRvResults
        tracksRecyclerView.layoutManager = LinearLayoutManager(this)
        tracksRecyclerView.adapter = tracksAdapter

        binding.searchBvClearHistory.setOnClickListener {
            historyInteractor.clear()
            hideHistory()
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

                if (placeholderIsVisible && s?.isEmpty() == true)
                    placeholderIsVisible =
                        placeholderManager(PlaceholderStatus.HIDDEN)

                if (!searchResultsIsVisible) {
                    if (inputEditText.hasFocus() && s?.isEmpty() == true) {
                        showHistory(historyInteractor.read() as ArrayList<Track>)
                    } else hideHistory()
                }
            }


            override fun afterTextChanged(s: Editable?) {
                searchInput = s.toString()
                if (inputEditText.hasFocus() && s?.isEmpty() == true) {
                    showHistory(historyInteractor.read() as ArrayList<Track>)
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
        foundTracks.clear()
        searchResultsIsVisible = hideSearchResults()
        placeholderIsVisible =
            placeholderManager(PlaceholderStatus.HIDDEN)
        showHistory(historyInteractor.read() as ArrayList<Track>)
    }

    private fun startSearch(expression: String) {
        placeholderIsVisible =
            placeholderManager(PlaceholderStatus.HIDDEN)
        binding.searchProgressBar.isVisible = true
        val tracksSearchUseCase = Creator.provideTracksSearchUseCase()
        tracksSearchUseCase.search(expression, object : TrackInteractor.TracksConsumer {
            //Выполнение происходит в другом потоке
            override fun consume(foundTracks: ArrayList<Track>?, errorMessage: String?) {
                runOnUiThread {
                    binding.searchProgressBar.isVisible = false
                    if (!foundTracks.isNullOrEmpty()) {
                        searchResultsIsVisible =
                            showSearchResults(foundTracks)
                    } else if (foundTracks != null && foundTracks.isEmpty()) {
                        placeholderManager(PlaceholderStatus.NOTHING_FOUND)
                    }

                    if (errorMessage != null) {
                        placeholderManager(PlaceholderStatus.NO_NETWORK)
                    }
                }
            }
        })
    }

    private fun placeholderManager(status: PlaceholderStatus): Boolean {
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
                    placeholderIsVisible = true
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
                    placeholderIsVisible = true
                }

                PlaceholderStatus.HIDDEN -> {
                    searchLlPlaceholder.isVisible = false
                    searchIvPlaceholderImage.isVisible = false
                    searchTvPlaceholderMessage.isVisible = false
                    searchTvPlaceholderExtraMessage.isVisible = false
                    searchBvPlaceholderButton.isVisible = false
                    placeholderIsVisible = false
                }
            }
        }
        return placeholderIsVisible
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun showHistory(searchHistory: ArrayList<Track>) {
        binding.apply {
            if (searchHistory.isNotEmpty()) {
                searchRvResults.isVisible = true
                searchBvClearHistory.isVisible = true
                searchTvSearchHistory.isVisible = true
                tracksAdapter.setHistoryVisibilityFlag(true)
                tracksAdapter.trackList = searchHistory
                tracksAdapter.notifyDataSetChanged()
            }
        }
    }

    private fun hideHistory() {
        binding.apply {
            searchRvResults.isVisible = false
            searchBvClearHistory.isVisible = false
            searchTvSearchHistory.isVisible = false
            tracksAdapter.setHistoryVisibilityFlag(false)
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun showSearchResults(foundTracks: ArrayList<Track>): Boolean {
        binding.searchRvResults.isVisible = true
        tracksAdapter.trackList = foundTracks
        tracksAdapter.notifyDataSetChanged()
        return true
    }


    private fun hideSearchResults(): Boolean {
        return false
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

