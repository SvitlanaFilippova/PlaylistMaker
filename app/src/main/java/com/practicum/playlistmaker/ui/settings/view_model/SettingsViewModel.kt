package com.practicum.playlistmaker.ui.settings.view_model


import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.practicum.playlistmaker.creator.Creator


class SettingsViewModel : ViewModel() {

    private val themeInteractor = Creator.provideThemeInteractor()
    private var darkThemeLiveData: MutableLiveData<Boolean> =
        MutableLiveData(themeInteractor.read())
    fun getDarkThemeLiveData(): LiveData<Boolean> = darkThemeLiveData


    fun saveTheme(checked: Boolean) {
        themeInteractor.save(checked)
        darkThemeLiveData.value = checked
    }



}