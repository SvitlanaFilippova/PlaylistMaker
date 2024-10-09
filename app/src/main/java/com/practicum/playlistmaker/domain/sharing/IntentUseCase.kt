package com.practicum.playlistmaker.domain.sharing

import android.content.Context

interface IntentUseCase {
    fun execute(context: Context)
    enum class IntentType {
        SHARE,
        SUPPORT,
        AGREEMENT
    }
}
