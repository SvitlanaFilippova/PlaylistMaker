package com.playlistmaker.app

import android.app.Application
import android.content.res.Configuration
import com.playlistmaker.domain.settings.ThemeInteractor
import com.playlistmaker.ui.presentation.di.dataModule
import com.playlistmaker.ui.presentation.di.domainModule
import com.playlistmaker.ui.presentation.di.repositoryModule
import com.playlistmaker.ui.presentation.di.uiModule
import com.playlistmaker.ui.presentation.di.viewModelModule
import org.koin.android.ext.android.inject
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin
import org.koin.core.logger.Level

class App : Application() {
    private val themeInteractor: ThemeInteractor by inject()

    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidLogger(Level.DEBUG)
            androidContext(this@App)
            modules(listOf(dataModule, viewModelModule, domainModule, uiModule, repositoryModule))
        }

        themeInteractor.switchTheme(checkTheme())
    }

    private fun checkTheme(): Boolean {
        if (!themeInteractor.wasThemeSetManually()) {
            val systemTheme =
                ((resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK) == Configuration.UI_MODE_NIGHT_YES)
            return systemTheme

        } else {
            return themeInteractor.read()
        }
    }
}



