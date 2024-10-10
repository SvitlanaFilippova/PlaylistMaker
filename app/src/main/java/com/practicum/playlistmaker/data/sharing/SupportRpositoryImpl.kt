package com.practicum.playlistmaker.data.sharing

import android.content.Context
import android.content.Intent
import android.net.Uri
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.domain.sharing.IntentRepository

class SupportRepositoryImpl(private val context: Context) : IntentRepository {
    override fun getIntent(): Intent {
        return Intent(Intent.ACTION_SENDTO).apply {
            data = Uri.parse(context.getString(R.string.settings_support_mailto))
            putExtra(
                Intent.EXTRA_EMAIL, arrayOf(context.getString(R.string.settings_support_email))
            )
            putExtra(
                Intent.EXTRA_SUBJECT, context.getString(R.string.settings_support_subject)
            )
            putExtra(
                Intent.EXTRA_TEXT, context.getString(R.string.settings_support_message)
            )
            flags = Intent.FLAG_ACTIVITY_NEW_TASK

        }
    }
}