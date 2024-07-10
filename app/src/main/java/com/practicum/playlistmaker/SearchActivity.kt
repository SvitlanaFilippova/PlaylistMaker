package com.practicum.playlistmaker

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.practicum.playlistmaker.databinding.ActivitySearchBinding


class SearchActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySearchBinding
    private var searchInput: String = INPUT_DEF


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


        val recyclerView = binding.searchRcSearchResults
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = SearchResultsAdapter()

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
