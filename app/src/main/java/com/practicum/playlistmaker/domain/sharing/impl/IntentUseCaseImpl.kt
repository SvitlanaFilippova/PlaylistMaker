package com.practicum.playlistmaker.domain.sharing.impl

import android.content.Context
import com.practicum.playlistmaker.domain.sharing.IntentRepository
import com.practicum.playlistmaker.domain.sharing.IntentUseCase


class IntentUseCaseImpl(private val repository: IntentRepository, private val context: Context) :
    IntentUseCase {

    override fun execute() {
        context.startActivity(repository.getIntent())


    }
}