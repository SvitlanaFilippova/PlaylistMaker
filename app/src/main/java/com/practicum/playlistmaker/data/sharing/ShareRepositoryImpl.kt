package com.practicum.playlistmaker.data.sharing

import android.content.Context
import android.content.Intent
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.domain.sharing.IntentRepository

class ShareRepositoryImpl(private val context: Context) : IntentRepository {
    override fun getIntent(): Intent {
        return Intent(Intent.ACTION_SEND).apply {
            setType("text/plain")
            putExtra(Intent.EXTRA_TEXT, context.getString(R.string.settings_share_message))
            flags = Intent.FLAG_ACTIVITY_NEW_TASK

        }

    }
}

