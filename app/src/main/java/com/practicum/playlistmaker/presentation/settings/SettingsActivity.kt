package com.practicum.playlistmaker.presentation.settings

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.practicum.playlistmaker.Creator
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.databinding.ActivitySettingsBinding
import com.practicum.playlistmaker.domain.api.IntentUseCase


class SettingsActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySettingsBinding

    @SuppressLint("UseSwitchCompatOrMaterialCode")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySettingsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        Creator.init(applicationContext, binding)
        binding.settingsToolbar.setNavigationOnClickListener {
            finish()
        }
        val settingsInteractor = Creator.provideThemeInteractor()

        val themeSwitcher = binding.swDarkTheme
        themeSwitcher.isChecked = settingsInteractor.read()
        themeSwitcher.setOnCheckedChangeListener { _, checked ->
            settingsInteractor.save(checked)
        }


        binding.tvShare.setOnClickListener {
            Creator.provideIntentUseCase(IntentType.SHARE).execute(context = this)
        }

        val rowSupport = binding.tvSupport
        rowSupport.setOnClickListener {
            Creator.provideIntentUseCase(IntentType.SUPPORT).execute(context = this)
        }

        val rowAgreement = binding.tvAgreement
        rowAgreement.setOnClickListener {
            Creator.provideIntentUseCase(IntentType.AGREEMENT).execute(context = this)

        }
    }

    enum class IntentType {
        SHARE,
        SUPPORT,
        AGREEMENT
    }
}
