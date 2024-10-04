package com.practicum.playlistmaker.domain.impl

import com.practicum.playlistmaker.domain.api.ThemeInteractor
import com.practicum.playlistmaker.domain.api.ThemeRepository


class ThemeInteractorImpl(private val repository: ThemeRepository) : ThemeInteractor {
    override fun read(): Boolean {
        return repository.read()
    }

    override fun save(isChecked: Boolean) {
        repository.save(isChecked)
    }
}