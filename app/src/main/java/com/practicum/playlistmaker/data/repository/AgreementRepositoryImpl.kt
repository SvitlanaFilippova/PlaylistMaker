package com.practicum.playlistmaker.data.repository

import android.content.Context
import android.content.Intent
import android.net.Uri
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.domain.api.IntentRepository

class AgreementRepositoryImpl(private val context: Context) : IntentRepository {
    override fun getIntent(): Intent {
        return Intent(
            Intent.ACTION_VIEW,
            Uri.parse(context.getString(R.string.settings_agreement_url))
        )
    }
}