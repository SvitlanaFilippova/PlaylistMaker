package com.practicum.playlistmaker

import android.annotation.SuppressLint
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.inputmethod.EditorInfo
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import com.practicum.playlistmaker.databinding.ActivitySearchBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


class SearchActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySearchBinding
    private var searchInput: String = INPUT_DEF
    val searchResultsAdapter = SearchResultsAdapter(trackListOfSearchResults)
    val searchHistoryAdapter = SearchResultsAdapter(trackListSearchHistory)
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

        val inputEditText = binding.searchEtInputSeacrh
        val searchClearButton = binding.searchIvClearIcon
        val placeholderUpdateButton = binding.searchBvPlaceholderButton

        if (searchInput.isNotEmpty()) {
            inputEditText.setText(searchInput)
        }

        searchClearButton.setOnClickListener {
            inputEditText.setText("")
            hideKeyboard()
            trackListOfSearchResults.clear()
            searchResultsAdapter.notifyDataSetChanged()
            placeholderVisibility(PlaceholderStatus.DEFAULT)
        }

        binding.searchToolbar.setNavigationOnClickListener() {
            finish()
        }

        placeholderUpdateButton.setOnClickListener {
            searchInITunes(inputEditText.text.toString())
        }


        binding.searchEtInputSeacrh.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                if (inputEditText.text.isNotEmpty()) {
                    searchInITunes(inputEditText.text.toString())
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
        trackSearchResultsRV.adapter = searchResultsAdapter

        val trackSearchHistoryRV = binding.searchRvHistory
        trackSearchHistoryRV.layoutManager = LinearLayoutManager(this)
        trackSearchHistoryRV.adapter = searchHistoryAdapter
    }

    fun searchInITunes(text: String) {
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
                                searchResultsAdapter.notifyDataSetChanged()
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
            searchResultsAdapter.notifyDataSetChanged()

            if (additionalMessage.isNotEmpty()) {
                searchIvPlaceholderImage.setImageResource(R.drawable.ic_no_internet)
                searchTvPlaceholderExtraMessage.text = additionalMessage
                placeholderVisibility(PlaceholderStatus.NO_NETWORK)
            }
        } else {
            placeholderVisibility(PlaceholderStatus.DEFAULT)
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
