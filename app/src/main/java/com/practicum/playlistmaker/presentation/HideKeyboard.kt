package com.practicum.playlistmaker.presentation

import android.content.Context
import android.view.View
import android.view.inputmethod.InputMethodManager

object HideKeyboard {
    fun execute(context: Context, view: View) {
        val inputMethodManager =
            context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.hideSoftInputFromWindow(view.windowToken, 0)
    }

}
