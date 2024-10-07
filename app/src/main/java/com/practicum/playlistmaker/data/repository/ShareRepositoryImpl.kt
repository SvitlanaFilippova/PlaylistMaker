package com.practicum.playlistmaker.data.repository

import android.content.Context
import android.content.Intent
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.domain.api.IntentRepository

class ShareRepositoryImpl(private val context: Context) : IntentRepository {
    override fun getIntent(): Intent {
        return Intent(Intent.ACTION_SEND).apply {
            setType("text/plain")
            putExtra(Intent.EXTRA_TEXT, context.getString(R.string.settings_share_message))
        }

    }
}

