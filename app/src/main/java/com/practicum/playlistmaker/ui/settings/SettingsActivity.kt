package com.practicum.playlistmaker.ui.settings

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.practicum.playlistmaker.databinding.ActivitySettingsBinding
import com.practicum.playlistmaker.ui.presentation.IntentManager
import com.practicum.playlistmaker.ui.presentation.IntentType
import com.practicum.playlistmaker.ui.settings.view_model.SettingsViewModel


class SettingsActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySettingsBinding
    private val vm: SettingsViewModel by lazy {
        ViewModelProvider(this)[SettingsViewModel::class.java]
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySettingsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        applyOnClickListeners()
    }


    @SuppressLint("UseSwitchCompatOrMaterialCode")
    private fun applyOnClickListeners() {
        binding.apply {
            settingsToolbar.setNavigationOnClickListener {
                finish()
            }
            setSwitcherState()
            swDarkTheme.setOnCheckedChangeListener { _, checked ->
                vm.saveTheme(checked)
            }

            tvShare.setOnClickListener {
                IntentManager.startIntentByType(IntentType.SHARE, this@SettingsActivity)
            }

            tvSupport.setOnClickListener {
                IntentManager.startIntentByType(IntentType.SUPPORT, this@SettingsActivity)
            }

            tvAgreement.setOnClickListener {
                IntentManager.startIntentByType(IntentType.AGREEMENT, this@SettingsActivity)
            }
        }
    }

    private fun setSwitcherState() {
        val switcherState = vm.getDarkThemeLiveData().value
        if (switcherState != null) {
            binding.swDarkTheme.isChecked = switcherState

            vm.getDarkThemeLiveData().observe(this) {
                binding.swDarkTheme.isChecked
            }
        }
    }
}
