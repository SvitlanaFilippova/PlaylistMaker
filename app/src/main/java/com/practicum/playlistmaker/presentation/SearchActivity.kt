package com.practicum.playlistmaker.presentation

import android.annotation.SuppressLint
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.text.TextWatcher
import android.view.inputmethod.EditorInfo
import android.widget.ProgressBar
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import com.practicum.playlistmaker.Creator
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.databinding.ActivitySearchBinding
import com.practicum.playlistmaker.domain.api.HistoryInteractor
import com.practicum.playlistmaker.domain.api.TracksInteractor
import com.practicum.playlistmaker.domain.models.Track


class SearchActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySearchBinding
    private var searchInput: String = INPUT_DEF
    private lateinit var historyInteractor: HistoryInteractor
    val tracksAdapter: TrackListAdapter by lazy {
        TrackListAdapter(historyInteractor)
    }
    private var mainThreadHandler: Handler? = null
    lateinit var progressBar: ProgressBar
    private var foundTracks = ArrayList<Track>()
    var searchResultsIsVisible = false
    var placeholderIsVisible = false


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySearchBinding.inflate(layoutInflater)
        setContentView(binding.root)
        mainThreadHandler = Handler(Looper.getMainLooper())
        historyInteractor = Creator.provideHistoryInteractor()
        progressBar = binding.searchProgressBar
        val searchClearButton = binding.searchIvClearIcon
        val inputEditText = binding.searchEtInputSeacrh
        val searchRunnable = Runnable { startSearch(inputEditText.text.toString()) }

        if (searchInput.isNotEmpty()) {
            inputEditText.setText(searchInput)
        }


        searchClearButton.setOnClickListener {
            inputEditText.setText("")
            HideKeyboard.execute(applicationContext, inputEditText)
            mainThreadHandler?.removeCallbacks(searchRunnable)
            foundTracks.clear()
            searchResultsIsVisible = hideSearchResults()
            placeholderIsVisible =
                placeholderManager(PlaceholderStatus.HIDDEN)
            showHistory(historyInteractor.read())
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
                    mainThreadHandler?.removeCallbacks(searchRunnable)
                    startSearch(inputEditText.text.toString())
                }
            }
            false
        }


        inputEditText.setOnFocusChangeListener { view, hasFocus ->
            if (!searchResultsIsVisible) {
                if (hasFocus && inputEditText.text.isEmpty()) {
                    showHistory(historyInteractor.read())
                } else hideHistory()
            }

        }

        fun searchDebounce() {
            mainThreadHandler?.removeCallbacks(searchRunnable)
            mainThreadHandler?.postDelayed(searchRunnable, SEARCH_DEBOUNCE_DELAY)
        }


        val searchTextWatcher = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                searchClearButton.isVisible = !s.isNullOrEmpty()
                if (s.isNullOrEmpty()) {
                    mainThreadHandler?.removeCallbacks(searchRunnable)
                } else searchDebounce()

                if (placeholderIsVisible && s?.isEmpty() == true)
                    placeholderIsVisible =
                        placeholderManager(PlaceholderStatus.HIDDEN)

                if (!searchResultsIsVisible) {
                    if (inputEditText.hasFocus() && s?.isEmpty() == true) {
                        showHistory(historyInteractor.read())
                    } else hideHistory()
                }
            }


            override fun afterTextChanged(s: Editable?) {
                searchInput = s.toString()
                if (inputEditText.hasFocus() && s?.isEmpty() == true) {
                    showHistory(historyInteractor.read())
                } else hideHistory()
            }
        }

        inputEditText.addTextChangedListener(searchTextWatcher)

        val tracksRecyclerView = binding.searchRvResults
        tracksRecyclerView.layoutManager = LinearLayoutManager(this)
        tracksRecyclerView.adapter = tracksAdapter

        binding.searchBvClearHistory.setOnClickListener {
            historyInteractor.clear()
            hideHistory()
        }
    }


    private fun startSearch(expression: String) {
        placeholderIsVisible =
            placeholderManager(PlaceholderStatus.HIDDEN)
        progressBar.isVisible = true

        Creator.provideTracksInteractor()
            .searchTracks(expression, object : TracksInteractor.TracksConsumer {
                //Выполнение происходит в другом потоке

                override fun consume(foundTracks: ArrayList<Track>) {
                    runOnUiThread {
                        progressBar.isVisible = false
                        searchResultsIsVisible =
                            showSearchResults(foundTracks)
                    }
                }

                override fun onError(resultCode: Int) {
                    runOnUiThread {
                        progressBar.isVisible = false
                        if (resultCode == 200) {
                            placeholderManager(PlaceholderStatus.NOTHING_FOUND)

                        } else {
                            placeholderManager(PlaceholderStatus.NO_NETWORK)
                        }
                        foundTracks.clear()
                    }
                }
            })
    }

    fun placeholderManager(status: PlaceholderStatus): Boolean {
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
    fun showHistory(searchHistory: ArrayList<Track>) {
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

    fun hideHistory() {
        binding.apply {
            searchRvResults.isVisible = false
            searchBvClearHistory.isVisible = false
            searchTvSearchHistory.isVisible = false
            tracksAdapter.setHistoryVisibilityFlag(false)
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    fun showSearchResults(foundTracks: ArrayList<Track>): Boolean {
        binding.searchRvResults.isVisible = true
        tracksAdapter.trackList = foundTracks
        tracksAdapter.notifyDataSetChanged()
        var searchResultsIsVisible = true
        return searchResultsIsVisible
    }


    fun hideSearchResults(): Boolean {
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

    companion object {
        const val SEARCH_INPUT = "SEARCH_INPUT"
        const val INPUT_DEF = ""
        private const val SEARCH_DEBOUNCE_DELAY = 2000L

        enum class PlaceholderStatus {
            NOTHING_FOUND,
            NO_NETWORK,
            HIDDEN
        }
    }
}

