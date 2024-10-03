package com.practicum.playlistmaker.presentation.search

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
import com.practicum.playlistmaker.databinding.ActivitySearchBinding
import com.practicum.playlistmaker.domain.api.HistoryInteractor
import com.practicum.playlistmaker.domain.api.TracksInteractor
import com.practicum.playlistmaker.domain.models.Track
import com.practicum.playlistmaker.presentation.HideKeyboard

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
    lateinit var placeholderManager: PlaceholderManager
    lateinit var historyVisibilityManager: HistoryVisibilityManager
    lateinit var searchResultsVisibilityManager: SearchResultsVisibilityManager


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySearchBinding.inflate(layoutInflater)
        setContentView(binding.root)
        mainThreadHandler = Handler(Looper.getMainLooper())
        Creator.init(applicationContext)
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
            searchResultsIsVisible = searchResultsVisibilityManager.hide()
            placeholderIsVisible =
                placeholderManager.execute(PlaceholderManager.PlaceholderStatus.HIDDEN)
            historyVisibilityManager.show(historyInteractor.read())
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
                    historyVisibilityManager.show(historyInteractor.read())
                } else historyVisibilityManager.hide()
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
                        placeholderManager.execute(PlaceholderManager.PlaceholderStatus.HIDDEN)

                if (!searchResultsIsVisible) {
                    if (inputEditText.hasFocus() && s?.isEmpty() == true) {
                        historyVisibilityManager.show(historyInteractor.read())
                    } else historyVisibilityManager.hide()
                }
            }


            override fun afterTextChanged(s: Editable?) {
                searchInput = s.toString()
                if (inputEditText.hasFocus() && s?.isEmpty() == true) {
                    historyVisibilityManager.show(historyInteractor.read())
                } else historyVisibilityManager.hide()
            }
        }

        inputEditText.addTextChangedListener(searchTextWatcher)

        val tracksRecyclerView = binding.searchRvResults
        tracksRecyclerView.layoutManager = LinearLayoutManager(this)
        tracksRecyclerView.adapter = tracksAdapter

        binding.searchBvClearHistory.setOnClickListener {
            historyInteractor.clear()
            historyVisibilityManager.hide()
        }

        placeholderManager = with(binding) {
            Creator.providePlaceholderManager(
                searchLlPlaceholder,
                searchIvPlaceholderImage,
                searchTvPlaceholderMessage,
                searchTvPlaceholderExtraMessage,
                searchBvPlaceholderButton
            )
        }

        historyVisibilityManager = with(binding) {
            Creator.provideHistoryVisibilityManager(
                searchRvResults,
                searchBvClearHistory,
                searchTvSearchHistory,
                tracksAdapter
            )
        }

        searchResultsVisibilityManager =
            Creator.provideSearchResultsVisibilityManager(binding.searchRvResults, tracksAdapter)
    }

    private fun startSearch(expression: String) {
        placeholderIsVisible =
            placeholderManager.execute(PlaceholderManager.PlaceholderStatus.HIDDEN)
        progressBar.isVisible = true

        Creator.provideTracksInteractor()
            .searchTracks(expression, object : TracksInteractor.TracksConsumer {
                //Выполнение происходит в другом потоке

                override fun consume(foundTracks: ArrayList<Track>) {
                    runOnUiThread {
                        progressBar.isVisible = false
                        searchResultsIsVisible = searchResultsVisibilityManager.show(foundTracks)
                    }
                }

                override fun onError(resultCode: Int) {
                    runOnUiThread {
                        progressBar.isVisible = false
                        if (resultCode == 200) {
                            placeholderManager.execute(PlaceholderManager.PlaceholderStatus.NOTHING_FOUND)

                        } else {
                            placeholderManager.execute(PlaceholderManager.PlaceholderStatus.NO_NETWORK)
                        }
                        foundTracks.clear()
                    }
                }
            })
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
        const val SEARCH_INPUT = "SEARCH_INPUT"
        const val INPUT_DEF = ""
        private const val SEARCH_DEBOUNCE_DELAY = 2000L
    }
}
