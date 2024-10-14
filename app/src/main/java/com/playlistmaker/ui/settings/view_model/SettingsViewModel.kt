package com.playlistmaker.ui.settings.view_model

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.playlistmaker.domain.settings.ThemeInteractor


class SettingsViewModel(private val themeInteractor: ThemeInteractor) : ViewModel() {


    private var darkThemeLiveData: MutableLiveData<Boolean> =
        MutableLiveData()

    fun getDarkThemeLiveData(): LiveData<Boolean> = darkThemeLiveData


    fun saveTheme(checked: Boolean) {
        themeInteractor.save(checked)
        darkThemeLiveData.value = checked
    }

    fun checkTheme(systemTheme: Boolean) {
        if (!themeInteractor.wasThemeSetManually()) {
            darkThemeLiveData.value = systemTheme
        } else {
            darkThemeLiveData.value = themeInteractor.read()
        }
    }


}