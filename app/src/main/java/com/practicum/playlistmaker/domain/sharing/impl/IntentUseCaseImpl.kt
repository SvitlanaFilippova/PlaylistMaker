package com.practicum.playlistmaker.domain.sharing.impl

import android.content.Context
import com.practicum.playlistmaker.domain.sharing.IntentRepository
import com.practicum.playlistmaker.domain.sharing.IntentUseCase


class IntentUseCaseImpl(private val repository: IntentRepository) : IntentUseCase {

    override fun execute(context: Context) {
        context.startActivity(repository.getIntent())


    }
}