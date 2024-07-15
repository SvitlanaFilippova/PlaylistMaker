package com.practicum.playlistmaker

import android.annotation.SuppressLint
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.inputmethod.EditorInfo
import android.widget.Toast
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
    val adapter = SearchResultsAdapter()
    private val searchBaseUrl = "https://itunes.apple.com"
    val retrofit = Retrofit.Builder()
        .baseUrl(searchBaseUrl)
        .addConverterFactory(GsonConverterFactory.create())
        .build()
    private val iTunesService = retrofit.create(ITunesApi::class.java)

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        binding = ActivitySearchBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val inputEditText = binding.searchEtInputSeacrh
        val searchClearButton = binding.searchIvClearIcon


        searchClearButton.setOnClickListener {
            inputEditText.setText("")
            hideKeyboard()
        }

        binding.searchToolbar.setNavigationOnClickListener() {
            finish()
        }

        if (searchInput.isNotEmpty()) {
            inputEditText.setText(searchInput)
        }

        val searchTextWatcher = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                // empty
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                searchClearButton.isVisible = !s.isNullOrEmpty()
            }

            override fun afterTextChanged(s: Editable?) {
                searchInput = s.toString()
            }
        }

        inputEditText.addTextChangedListener(searchTextWatcher)


        val tracksRecView = binding.searchRcSearchResults
        tracksRecView.layoutManager = LinearLayoutManager(this)
        tracksRecView.adapter = adapter


        binding.searchEtInputSeacrh.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                if (inputEditText.text.isNotEmpty()) {

                    iTunesService.search(inputEditText.text.toString())
                        .enqueue(object : Callback<SongsResponse> {
                            @SuppressLint("NotifyDataSetChanged")
                            override fun onResponse(
                                call: Call<SongsResponse>,
                                response: Response<SongsResponse>
                            ) {
                                if (response.code() == 200) {
                                    trackList.clear()
                                    if (response.body()?.results?.isNotEmpty() == true) {
                                        trackList.addAll(response.body()?.results!!)
                                        adapter.notifyDataSetChanged()
                                    }
                                    if (trackList.isEmpty()) {
                                        showMessage(
                                            getString(R.string.search_error_nothing_found),
                                            ""
                                        )

                                    } else {
                                        showMessage("", "")
                                    }
                                } else {
                                    showMessage(
                                        getString(R.string.search_error_netwwork),
                                        getString(R.string.search_error_netwwork_extra)
                                    )
                                }
                            }


                            override fun onFailure(p0: Call<SongsResponse>, p1: Throwable) {
                                showMessage(
                                    getString(R.string.search_error_netwwork),
                                    getString(R.string.search_error_netwwork_extra)
                                )
                            }

                        })
                }// ВЫПОЛНЯЙТЕ ПОИСКОВЫЙ ЗАПРОС ЗДЕСЬ

            }
            false
        }


    }

    fun showMessage(text: String, additionalMessage: String) = with(binding) {
        if (text.isNotEmpty()) {
            searchIvPlaceholderImage.setImageResource(R.drawable.ic_nothing_found)
            searchTvPlaceholderMessage.text = text
            placeholderVisibility(true, true, false, false)
            trackList.clear()
            adapter.notifyDataSetChanged()

            if (additionalMessage.isNotEmpty()) {
                searchIvPlaceholderImage.setImageResource(R.drawable.ic_no_internet)
                searchTvPlaceholderExtraMessage.text = additionalMessage
                placeholderVisibility(true, true, true, true)
            }
        } else {
            placeholderVisibility(false, false, false, false)
        }
    }

    fun placeholderVisibility(
        imageVisibility: Boolean,
        messageVisibility: Boolean,
        extraMessageVisibility: Boolean,
        buttonVisibility: Boolean,
    ) = with(binding) {
        searchIvPlaceholderImage.isVisible = imageVisibility
        searchTvPlaceholderMessage.isVisible = messageVisibility
        searchTvPlaceholderExtraMessage.isVisible = extraMessageVisibility
        searchBvPlaceholderButton.isVisible = buttonVisibility
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


}
