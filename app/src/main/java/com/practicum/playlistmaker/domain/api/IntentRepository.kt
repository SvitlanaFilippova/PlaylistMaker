package com.practicum.playlistmaker.domain.api

import android.content.Intent

interface IntentRepository {
    fun getIntent(): Intent
}