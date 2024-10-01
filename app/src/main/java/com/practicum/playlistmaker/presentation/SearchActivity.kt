package com.practicum.playlistmaker.presentation

import android.annotation.SuppressLint
import android.content.SharedPreferences
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import com.practicum.playlistmaker.Creator
import com.practicum.playlistmaker.PLAYLISTMAKER_PREFERENCES
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.SearchHistory
import com.practicum.playlistmaker.databinding.ActivitySearchBinding
import com.practicum.playlistmaker.domain.api.TracksInteractor
import com.practicum.playlistmaker.domain.models.Track
import com.practicum.playlistmaker.hideKeyboard
import com.practicum.playlistmaker.trackListSearchHistory
import retrofit2.Callback


class SearchActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySearchBinding
    private var searchInput: String = INPUT_DEF
    val tracksAdapter = SearchResultsAdapter()
    private var mainThreadHandler: Handler? = null
    lateinit var progressBar: View
    var tracks = ArrayList<Track>()


    var searchResultsIsVisible = false
    var placeholderIsVisible = false


    @SuppressLint("NotifyDataSetChanged")
    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        binding = ActivitySearchBinding.inflate(layoutInflater)
        setContentView(binding.root)

        mainThreadHandler = Handler(Looper.getMainLooper())
        val sharedPreferences = getSharedPreferences(PLAYLISTMAKER_PREFERENCES, MODE_PRIVATE)
        tracksAdapter.sharedPreferences = sharedPreferences

        getHistory(sharedPreferences)


        val inputEditText = binding.searchEtInputSeacrh
        val searchRunnable = Runnable { startSearch(inputEditText.text.toString()) }

        if (searchInput.isNotEmpty()) {
            inputEditText.setText(searchInput)
        }
        val searchClearButton = binding.searchIvClearIcon
        progressBar = binding.searchProgressBar

        searchClearButton.setOnClickListener {
            inputEditText.setText("")
            hideKeyboard()
            mainThreadHandler?.removeCallbacks(searchRunnable)
            tracks.clear()
            searchResultsIsVisible = false
            placeholderVisibility(PlaceholderStatus.HIDDEN)
            getHistory(sharedPreferences)
            showHistory()
            tracksAdapter.notifyDataSetChanged()

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
                if (hasFocus && inputEditText.text.isEmpty()) showHistory() else hideHistory()
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
                } else {
                    searchDebounce()
                }

                if (placeholderIsVisible && s?.isEmpty() == true) placeholderVisibility(
                    PlaceholderStatus.HIDDEN
                )
                if (!searchResultsIsVisible) {
                    if (inputEditText.hasFocus() && s?.isEmpty() == true) {
                        getHistory(sharedPreferences)
                        showHistory()
                        tracksAdapter.notifyDataSetChanged()
                    } else hideHistory()
                }
            }


            override fun afterTextChanged(s: Editable?) {
                searchInput = s.toString()

                if (inputEditText.hasFocus() && s?.isEmpty() == true) {
                    getHistory(sharedPreferences)
                    showHistory()
                    tracksAdapter.notifyDataSetChanged()
                } else hideHistory()

            }
        }

        inputEditText.addTextChangedListener(searchTextWatcher)


        val trackSearchResultsRV = binding.searchRvResults
        trackSearchResultsRV.layoutManager = LinearLayoutManager(this)
        trackSearchResultsRV.adapter = tracksAdapter

        val clearHistoryButton = binding.searchBvClearHistory
        val searchHistoryLogic = SearchHistory(sharedPreferences)
        clearHistoryButton.setOnClickListener {
            searchHistoryLogic.clearHistory()
            hideHistory()
        }
    }

    private fun startSearch(expression: String) {
        showProgressBar()
        Creator.provideTracksInteractor()
            .searchTracks(expression, object : TracksInteractor.TracksConsumer {
                //Выполнение происходит в другом потоке

                override fun consume(foundTracks: List<Track>) {
                    runOnUiThread {
                        showSearchResults(foundTracks)
                    }
                }

                override fun onError(resultCode: Int) {
                    runOnUiThread {
                        showMessage(resultCode)
                    }
                }
            })
    }

    fun showProgressBar() {
        hideHistory()
        placeholderVisibility(PlaceholderStatus.HIDDEN)
        progressBar.isVisible = true
        //TODO добавить сюда скрывание плейсхолдера, истории и результатов поиска
    }

    fun hideProgressBar() {
        progressBar.isVisible = false
    }

    fun showSearchResults(foundTracks: List<Track>) {
        hideProgressBar()
        tracks = foundTracks as ArrayList<Track>
        binding.searchRvResults.isVisible = true
        searchResultsIsVisible = true
        tracksAdapter.trackList = tracks
        tracksAdapter.notifyDataSetChanged()
    }

    @SuppressLint("NotifyDataSetChanged")
    fun showMessage(resultCode: Int) = with(binding) {
        hideProgressBar()

        if (resultCode == 200) {
            searchIvPlaceholderImage.setImageResource(R.drawable.ic_nothing_found)
            searchTvPlaceholderMessage.text = getString(R.string.search_error_nothing_found)
            placeholderVisibility(PlaceholderStatus.NOTHING_FOUND)
            tracksAdapter.notifyDataSetChanged()
            tracks.clear()

        } else {
            searchIvPlaceholderImage.setImageResource(R.drawable.ic_no_internet)
            searchTvPlaceholderMessage.text = getString(R.string.search_error_network)
            searchTvPlaceholderExtraMessage.text = getString(R.string.search_error_network_extra)
            placeholderVisibility(PlaceholderStatus.NO_NETWORK)
        }

    }


    fun hideHistory() = with(binding) {
        searchRvResults.isVisible = false
        searchBvClearHistory.isVisible = false
        searchTvSearchHistory.isVisible = false
        tracksAdapter.historyIsVisibleFlag = false

    }

    fun showHistory() = with(binding) {
        if (trackListSearchHistory.isNotEmpty()) {
            searchRvResults.isVisible = true
            searchBvClearHistory.isVisible = true
            searchTvSearchHistory.isVisible = true
            progressBar.isVisible = false
            tracksAdapter.historyIsVisibleFlag = true
        }
    }

    fun getHistory(sharedPreferences: SharedPreferences) {
        val searchHistory = SearchHistory(sharedPreferences)
        searchHistory.readHistory()
        tracksAdapter.trackList = trackListSearchHistory


    }


    private fun placeholderVisibility(status: PlaceholderStatus) = with(binding) {
        when (status) {
            PlaceholderStatus.NOTHING_FOUND -> {
                searchLlPlaceholder.isVisible = true
                searchIvPlaceholderImage.isVisible = true
                searchTvPlaceholderMessage.isVisible = true
                searchTvPlaceholderExtraMessage.isVisible = false
                searchBvPlaceholderButton.isVisible = false
                placeholderIsVisible = true

            }

            PlaceholderStatus.NO_NETWORK -> {
                searchLlPlaceholder.isVisible = true
                searchIvPlaceholderImage.isVisible = true
                searchTvPlaceholderMessage.isVisible = true
                searchTvPlaceholderExtraMessage.isVisible = true
                searchBvPlaceholderButton.isVisible = true
                progressBar.isVisible = false
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

    enum class PlaceholderStatus {
        NOTHING_FOUND,
        NO_NETWORK,
        HIDDEN
    }
}
