package com.practicum.playlistmaker

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.ListView
import android.widget.Switch
import android.widget.Toast
import com.practicum.playlistmaker.R

class SettingsActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)


        val iconBack = findViewById<ImageView>(R.id.iv_arrow_back)
        iconBack.setOnClickListener {
            val backIntent = Intent(this, MainActivity::class.java)
            startActivity(backIntent)
        }
        val rowDarkTheme = findViewById<LinearLayout>(R.id.ll_dark_theme)
        rowDarkTheme.setOnClickListener {
            // тут будет обработка клика по строке "Тёмная тема"
        }

            val rowShare = findViewById<LinearLayout>(R.id.ll_share)
        rowShare.setOnClickListener {
            // тут будет обработка клика по строке "Поделиться  приложением"
        }

        val rowSupport = findViewById<LinearLayout>(R.id.ll_support)
        rowSupport.setOnClickListener {
            // тут будет обработка клика по строке "Написать в поддержку"
        }
        val rowAgreement = findViewById<LinearLayout>(R.id.ll_agreement)
        rowAgreement.setOnClickListener {
        // тут будет обработка клика по строке "Польз соглашение"
        }
    }
    }

