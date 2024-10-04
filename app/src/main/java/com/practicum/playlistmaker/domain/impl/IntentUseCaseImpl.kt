package com.practicum.playlistmaker.domain.impl

import android.content.Context
import com.practicum.playlistmaker.domain.api.IntentRepository
import com.practicum.playlistmaker.domain.api.IntentUseCase


class IntentUseCaseImpl(val repository: IntentRepository) : IntentUseCase {

    override fun execute(context: Context) {
        context.startActivity(repository.getIntent())


    }
}