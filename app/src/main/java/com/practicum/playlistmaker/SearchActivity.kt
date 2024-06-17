package com.practicum.playlistmaker

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.EditText
import android.widget.ImageView
import com.practicum.playlistmaker.R

class SearchActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)
        val inputEditText = findViewById<EditText>(R.id.search_et_inputSeacrh)
        val clearButton = findViewById<ImageView>(R.id.search_iv_clearIcon)

        clearButton.setOnClickListener {
            inputEditText.setText("")
        }

        val iconBack = findViewById<ImageView>(R.id.search_iv_arrow_back)
        iconBack.setOnClickListener {
            val backIntent = Intent(this, MainActivity::class.java)
            startActivity(backIntent) //тут надо будет переделать, через финиш активити должно быть реализована, иначе может быть глюк. Как???
        }

        val searchTextWatcher = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                // empty
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

                clearButton.visibility = clearButtonVisibility(s)
            }

            override fun afterTextChanged(s: Editable?) {
                // empty
            }
        }
        inputEditText.addTextChangedListener(searchTextWatcher)

    }

    private fun clearButtonVisibility(s: CharSequence?): Int {
        return if (s.isNullOrEmpty()) {
            View.GONE
        } else {
            View.VISIBLE
        }
    }
}