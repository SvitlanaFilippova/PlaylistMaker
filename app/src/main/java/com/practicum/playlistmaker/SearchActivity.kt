package com.practicum.playlistmaker

import android.annotation.SuppressLint
import android.content.SharedPreferences
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.inputmethod.EditorInfo
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.practicum.playlistmaker.databinding.ActivitySearchBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


class SearchActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySearchBinding
    private var searchInput: String = INPUT_DEF
    val tracksAdapter = SearchResultsAdapter()

    private val searchBaseUrl = "https://itunes.apple.com"
    val retrofit = Retrofit.Builder()
        .baseUrl(searchBaseUrl)
        .addConverterFactory(GsonConverterFactory.create())
        .build()
    private val iTunesService = retrofit.create(ITunesApi::class.java)


    @SuppressLint("NotifyDataSetChanged")
    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        binding = ActivitySearchBinding.inflate(layoutInflater)
        setContentView(binding.root)


        val sharedPreferences = getSharedPreferences(PLAYLISTMAKER_PREFERENCES, MODE_PRIVATE)
        tracksAdapter.sharedPreferences = sharedPreferences

        getHistory(sharedPreferences)


        val inputEditText = binding.searchEtInputSeacrh

        if (searchInput.isNotEmpty()) {
            inputEditText.setText(searchInput)
        }
        val searchClearButton = binding.searchIvClearIcon

        searchClearButton.setOnClickListener {
            inputEditText.setText("")
            hideKeyboard()
            trackListOfSearchResults.clear()
            tracksAdapter.notifyDataSetChanged()
            placeholderVisibility(PlaceholderStatus.DEFAULT)
            getHistory(sharedPreferences)
            tracksAdapter.trackList = trackListSearchHistory


        }

        binding.searchToolbar.setNavigationOnClickListener() {
            finish()
        }

        val placeholderUpdateButton = binding.searchBvPlaceholderButton
        placeholderUpdateButton.setOnClickListener {
            searchInITunes(inputEditText.text.toString())
        }

        tracksAdapter.trackList = trackListSearchHistory

        binding.searchEtInputSeacrh.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                if (inputEditText.text.isNotEmpty()) {
                    searchInITunes(inputEditText.text.toString())
                    hideHistory()
                    tracksAdapter.trackList = trackListOfSearchResults
                }
            }
            false
        }

        val searchTextWatcher = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                searchClearButton.isVisible = !s.isNullOrEmpty()
            }


            override fun afterTextChanged(s: Editable?) {
                searchInput = s.toString()

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


    private fun searchInITunes(text: String) {
        iTunesService.search(text)
            .enqueue(object : Callback<SongsResponse> {
                @SuppressLint("NotifyDataSetChanged")
                override fun onResponse(
                    call: Call<SongsResponse>,
                    response: Response<SongsResponse>
                ) {
                    if (response.code() == 200) {
                        trackListOfSearchResults.clear()
                        var results = response.body()?.results
                        if (results != null) {
                            if (results.isNotEmpty()) {
                                trackListOfSearchResults.addAll(results)
                                tracksAdapter.notifyDataSetChanged()
                            }
                        }
                        if (trackListOfSearchResults.isEmpty()) {
                            showMessage(
                                getString(R.string.search_error_nothing_found),
                                ""
                            )

                        } else {
                            showMessage("", "")
                        }
                    } else {
                        showMessage(
                            getString(R.string.search_error_network),
                            getString(R.string.search_error_network_extra)
                        )
                    }
                }

                override fun onFailure(p0: Call<SongsResponse>, p1: Throwable) {
                    showMessage(
                        getString(R.string.search_error_network),
                        getString(R.string.search_error_network_extra)
                    )
                }
            })
    }


    @SuppressLint("NotifyDataSetChanged")
    fun showMessage(text: String, additionalMessage: String) = with(binding) {
        if (text.isNotEmpty()) {
            searchIvPlaceholderImage.setImageResource(R.drawable.ic_nothing_found)
            searchTvPlaceholderMessage.text = text
            placeholderVisibility(PlaceholderStatus.NOTHING_FOUND)
            trackListOfSearchResults.clear()
            tracksAdapter.notifyDataSetChanged()

            if (additionalMessage.isNotEmpty()) {
                searchIvPlaceholderImage.setImageResource(R.drawable.ic_no_internet)
                searchTvPlaceholderExtraMessage.text = additionalMessage
                placeholderVisibility(PlaceholderStatus.NO_NETWORK)
            }
        } else {
            placeholderVisibility(PlaceholderStatus.DEFAULT)
        }
    }

    fun hideHistory() = with(binding) {
        searchBvClearHistory.isVisible = false
        searchTvSearchHistory.isVisible = false
        tracksAdapter.historyIsVisible = false
    }

    fun showHistory() = with(binding) {
        searchBvClearHistory.isVisible = true
        searchTvSearchHistory.isVisible = true
        tracksAdapter.historyIsVisible = true
    }

    fun getHistory(sharedPreferences: SharedPreferences) {
        val searchHistory = SearchHistory(sharedPreferences)
        searchHistory.readHistory()
        tracksAdapter.trackList = trackListSearchHistory
        if (trackListSearchHistory.isNotEmpty()) {
            showHistory()
        }


    }


    private fun placeholderVisibility(status: PlaceholderStatus) = with(binding) {
        when (status) {
            PlaceholderStatus.NOTHING_FOUND -> {
                searchLlPlaceholder.isVisible = true
                searchIvPlaceholderImage.isVisible = true
                searchTvPlaceholderMessage.isVisible = true
                searchTvPlaceholderExtraMessage.isVisible = false
                searchBvPlaceholderButton.isVisible = false
            }

            PlaceholderStatus.NO_NETWORK -> {
                searchLlPlaceholder.isVisible = true
                searchIvPlaceholderImage.isVisible = true
                searchTvPlaceholderMessage.isVisible = true
                searchTvPlaceholderExtraMessage.isVisible = true
                searchBvPlaceholderButton.isVisible = true
            }

            PlaceholderStatus.DEFAULT -> {
                searchLlPlaceholder.isVisible = false
                searchIvPlaceholderImage.isVisible = false
                searchTvPlaceholderMessage.isVisible = false
                searchTvPlaceholderExtraMessage.isVisible = false
                searchBvPlaceholderButton.isVisible = false

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
    }

    enum class PlaceholderStatus {
        NOTHING_FOUND,
        NO_NETWORK,
        DEFAULT
    }
}
