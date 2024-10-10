package com.practicum.playlistmaker.data.sharing

import android.content.Context
import android.content.Intent
import android.net.Uri
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.domain.sharing.IntentRepository

class AgreementRepositoryImpl(private val context: Context) : IntentRepository {
    override fun getIntent(): Intent {
        return Intent(
            Intent.ACTION_VIEW,

            Uri.parse(context.getString(R.string.settings_agreement_url))
        ).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK
        }
    }
}