package com.practicum.playlistmaker

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity


class SettingsActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

         val iconBack = findViewById<ImageView>(R.id.settings_iv_arrow_back)
        iconBack.setOnClickListener {
            val backIntent = Intent(this, MainActivity::class.java)
            startActivity(backIntent) //тут надо будет переделать, через финиш активити должно быть реализована, иначе может быть глюк. Как???
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
            val supportIntent = Intent(Intent.ACTION_SENDTO)
            supportIntent.data = Uri.parse(getString(R.string.settings_support_mailto))
            supportIntent.putExtra(Intent.EXTRA_EMAIL, arrayOf(getString(R.string.settings_support_email)))
            supportIntent.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.settings_support_subject))
            supportIntent.putExtra(Intent.EXTRA_TEXT, getString(R.string.settings_support_message))
            startActivity(supportIntent)
        }
        val rowAgreement = findViewById<LinearLayout>(R.id.ll_agreement)
        rowAgreement.setOnClickListener {
            val agreementIntent = Intent(Intent.ACTION_VIEW, Uri.parse(getString(R.string.settings_agreement_url)))
            startActivity(agreementIntent)

        }
    }
    }

