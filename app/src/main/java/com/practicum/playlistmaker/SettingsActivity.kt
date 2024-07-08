package com.practicum.playlistmaker

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import com.practicum.playlistmaker.databinding.ActivitySearchBinding
import com.practicum.playlistmaker.databinding.ActivitySettingsBinding


class SettingsActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySettingsBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySettingsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.settingsToolbar.setNavigationOnClickListener() {
            finish()
        }



        val rowDarkTheme = findViewById<LinearLayout>(R.id.ll_dark_theme)
        rowDarkTheme.setOnClickListener {
            // тут будет обработка клика по строке "Тёмная тема"
        }

            val rowShare = findViewById<LinearLayout>(R.id.ll_share)
        rowShare.setOnClickListener {
            val shareIntent = Intent(Intent.ACTION_SEND)
            shareIntent.setType("text/plain")
             shareIntent.putExtra(Intent.EXTRA_TEXT, getString(R.string.settings_share_message))
            startActivity(Intent.createChooser(shareIntent, getString(R.string.settings_share_title)))
        }

        val rowSupport = findViewById<LinearLayout>(R.id.ll_support)
        rowSupport.setOnClickListener {

            Intent(Intent.ACTION_SENDTO).apply {
                data = Uri.parse(getString(R.string.settings_support_mailto))
                putExtra(Intent.EXTRA_EMAIL,arrayOf(getString(R.string.settings_support_email))
                )
                putExtra(Intent.EXTRA_SUBJECT,getString(R.string.settings_support_subject)
                )
                putExtra(Intent.EXTRA_TEXT,getString(R.string.settings_support_message)
                )

            }.also(::startActivity)
        }
        val rowAgreement = findViewById<LinearLayout>(R.id.ll_agreement)
        rowAgreement.setOnClickListener {
            val agreementIntent = Intent(Intent.ACTION_VIEW, Uri.parse(getString(R.string.settings_agreement_url)))
            startActivity(agreementIntent)

        }
    }
    }

