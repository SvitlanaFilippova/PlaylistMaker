package com.practicum.playlistmaker.presentation.settings

import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.practicum.playlistmaker.Creator
import com.practicum.playlistmaker.R

import com.practicum.playlistmaker.databinding.ActivitySettingsBinding


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


        val rowShare = binding.tvShare
        rowShare.setOnClickListener {
            val shareIntent = Intent(Intent.ACTION_SEND)
            shareIntent.setType("text/plain")
            shareIntent.putExtra(Intent.EXTRA_TEXT, getString(R.string.settings_share_message))
            startActivity(
                Intent.createChooser(
                    shareIntent,
                    getString(R.string.settings_share_title)
                )
            )
        }

        val rowSupport = binding.tvSupport
        rowSupport.setOnClickListener {

            Intent(Intent.ACTION_SENDTO).apply {
                data = Uri.parse(getString(R.string.settings_support_mailto))
                putExtra(
                    Intent.EXTRA_EMAIL, arrayOf(getString(R.string.settings_support_email))
                )
                putExtra(
                    Intent.EXTRA_SUBJECT, getString(R.string.settings_support_subject)
                )
                putExtra(
                    Intent.EXTRA_TEXT, getString(R.string.settings_support_message)
                )

            }.also(::startActivity)
        }

        val rowAgreement = binding.tvAgreement
        rowAgreement.setOnClickListener {
            val agreementIntent =
                Intent(Intent.ACTION_VIEW, Uri.parse(getString(R.string.settings_agreement_url)))
            startActivity(agreementIntent)

        }
    }
}
