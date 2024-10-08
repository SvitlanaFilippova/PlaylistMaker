package com.practicum.playlistmaker.domain.api

import android.content.Context

interface IntentUseCase {
    fun execute(context: Context)
    enum class IntentType {
        SHARE,
        SUPPORT,
        AGREEMENT
    }
}
