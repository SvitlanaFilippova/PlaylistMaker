package com.practicum.playlistmaker.ui.main

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.practicum.playlistmaker.databinding.ActivityMainBinding
import com.practicum.playlistmaker.ui.library.LibraryActivity
import com.practicum.playlistmaker.ui.search.SearchActivity
import com.practicum.playlistmaker.ui.settings.SettingsActivity

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        applyOnClickListeners()

    }

    private fun runSearchIntent() {
        val searchIntent = Intent(this@MainActivity, SearchActivity::class.java)
        startActivity(searchIntent)
    }

    private fun runLibraryIntent() {
        val libraryIntent = Intent(this@MainActivity, LibraryActivity::class.java)
        startActivity(libraryIntent)
    }

    private fun runSettingsIntent() {
        val settingsIntent = Intent(this@MainActivity, SettingsActivity::class.java)
        startActivity(settingsIntent)
    }

    private fun applyOnClickListeners() {
        binding.apply {
            btSearch.setOnClickListener {
                runSearchIntent()
            }
            btLibrary.setOnClickListener {
                runLibraryIntent()
            }
            btSettings.setOnClickListener {
                runSettingsIntent()
            }
        }

    }
}

