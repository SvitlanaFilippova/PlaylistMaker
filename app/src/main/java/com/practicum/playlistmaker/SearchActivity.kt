package com.practicum.playlistmaker

import android.app.Activity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity


class SearchActivity : AppCompatActivity() {
    private var searchInput: String = INPUT_DEF
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        savedInstanceState?.let {
            val str = it.getString(SEARCH_INPUT, searchInput)
            Log.i("Проверка сохранения поискового запроса", "string $str")
        }

        setContentView(R.layout.activity_search)
        val inputEditText = findViewById<EditText>(R.id.search_et_inputSeacrh)
        val clearButton = findViewById<ImageView>(R.id.search_iv_clearIcon)

        clearButton.setOnClickListener {
            inputEditText.setText("")
            hideSoftKeyboard(this) //   спрятать клавиатуру!
        }

        val iconBack = findViewById<ImageView>(R.id.search_iv_arrow_back)
        iconBack.setOnClickListener {
            finish()
        }
             if (searchInput != "") { inputEditText.setText(searchInput) }

        val searchTextWatcher = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                // empty
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

                clearButton.visibility = clearButtonVisibility(s)
            }

            override fun afterTextChanged(s: Editable?) {
                searchInput = s.toString()
            }
        }

        inputEditText.addTextChangedListener(searchTextWatcher)
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString(SEARCH_INPUT, searchInput)
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
         searchInput = savedInstanceState.getString(SEARCH_INPUT, searchInput)
    }

    private fun clearButtonVisibility(s: CharSequence?): Int {
        return if (s.isNullOrEmpty()) {
            View.GONE
        } else {
            View.VISIBLE
        }
    }
    companion object {
        const val SEARCH_INPUT = "SEARCH_INPUT"
        const val INPUT_DEF = ""
    }

    fun hideSoftKeyboard(activity: Activity) {
        val inputMethodManager = activity.getSystemService(
            INPUT_METHOD_SERVICE
        ) as InputMethodManager
        if (inputMethodManager.isAcceptingText) {
            inputMethodManager.hideSoftInputFromWindow(
                activity.currentFocus!!.windowToken,
                0
            )
        }
    }
}
