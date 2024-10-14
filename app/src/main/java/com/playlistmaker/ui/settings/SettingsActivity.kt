package com.playlistmaker.ui.settings


import android.content.res.Configuration
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.playlistmaker.ui.presentation.IntentManager
import com.playlistmaker.ui.presentation.IntentType
import com.playlistmaker.ui.settings.view_model.SettingsViewModel
import com.practicum.playlistmaker.databinding.ActivitySettingsBinding
import org.koin.androidx.viewmodel.ext.android.viewModel


class SettingsActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySettingsBinding
    private val vm by viewModel<SettingsViewModel>()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySettingsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSwitcherState()
        applyOnClickListeners()
    }


    private fun applyOnClickListeners() {
        binding.apply {
            settingsToolbar.setNavigationOnClickListener {
                finish()
            }

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
        systemThemeToVM(vm::checkTheme)
        val switcherState = vm.getDarkThemeLiveData().value
        if (switcherState != null) {
            binding.swDarkTheme.isChecked = switcherState

            vm.getDarkThemeLiveData().observe(this) {
                binding.swDarkTheme.isChecked
            }
        }
    }

    private fun systemThemeToVM(checkTheme: (Boolean) -> Unit) {
        checkTheme(
            ((resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK) == Configuration.UI_MODE_NIGHT_YES)
        )
    }


}
