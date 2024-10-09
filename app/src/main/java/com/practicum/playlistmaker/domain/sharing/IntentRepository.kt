package com.practicum.playlistmaker.domain.sharing

import android.content.Intent

interface IntentRepository {
    fun getIntent(): Intent
}