package com.playlistmaker.domain.settings.impl

import com.playlistmaker.domain.settings.ThemeInteractor
import com.playlistmaker.domain.settings.ThemeRepository


class ThemeInteractorImpl(private val repository: ThemeRepository) : ThemeInteractor {
    override fun read(): Boolean {
        return repository.read()
    }

    override fun save(isChecked: Boolean) {
        repository.save(isChecked)
    }

    override fun switchTheme(isChecked: Boolean) {
        repository.switchTheme(isChecked)
    }
    override fun wasThemeSetManually(): Boolean {
        return repository.wasThemeSetManually()
    }
}
