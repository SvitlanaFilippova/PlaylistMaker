package com.playlistmaker.ui.settings.view_model

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.playlistmaker.creator.Creator


class SettingsViewModel : ViewModel() {

    private val themeInteractor = Creator.provideThemeInteractor()
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