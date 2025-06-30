package com.playlistmaker.ui.util

import android.content.Context
import android.view.inputmethod.InputMethodManager
import android.widget.EditText


fun EditText.hideKeyBoard() {
    val inputMethodManager =
        this.context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
    inputMethodManager.hideSoftInputFromWindow(this.windowToken, 0)
}


