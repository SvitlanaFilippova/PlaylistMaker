package com.practicum.playlistmaker.presentation.settings

import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.practicum.playlistmaker.App
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.THEME_KEY
import com.practicum.playlistmaker.data.storage.PLAYLISTMAKER_PREFERENCES

import com.practicum.playlistmaker.databinding.ActivitySettingsBinding


class SettingsActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySettingsBinding

    @SuppressLint("UseSwitchCompatOrMaterialCode")
    override fun onCreate(savedInstanceState: Bundle?) {
        val sharedPrefs = getSharedPreferences(PLAYLISTMAKER_PREFERENCES, MODE_PRIVATE)
        super.onCreate(savedInstanceState)
        binding = ActivitySettingsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.settingsToolbar.setNavigationOnClickListener() {
            finish()
        }


        val themeSwitcher = binding.swDarkTheme
        themeSwitcher.setOnCheckedChangeListener { switcher, checked ->
            (applicationContext as App).switchTheme(checked)
            sharedPrefs.edit().putBoolean(THEME_KEY, checked).apply()

        }

        if (sharedPrefs.getBoolean(THEME_KEY, false)) {
            themeSwitcher.isChecked = true

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
