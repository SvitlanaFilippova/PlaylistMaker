package com.practicum.playlistmaker

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val buttonSearch = findViewById<com.google.android.material.button.MaterialButton>(R.id.bt_search)
        val buttonLibrary = findViewById<com.google.android.material.button.MaterialButton>(R.id.bt_library)
        val buttonSettings = findViewById<com.google.android.material.button.MaterialButton>(R.id.bt_settings)

        val buttonSearchClickListener: View.OnClickListener = object : View.OnClickListener {

            override fun onClick(v: View?) {
                               val searchIntent = Intent(this@MainActivity, SearchActivity::class.java)
                startActivity(searchIntent)
        }
        }

        buttonSearch.setOnClickListener(buttonSearchClickListener)

        buttonLibrary.setOnClickListener {
            val libraryIntent = Intent(this, LibraryActivity::class.java)
            startActivity(libraryIntent)
        }
        buttonSettings.setOnClickListener {
            val settingsIntent = Intent(this, SettingsActivity::class.java)
            startActivity(settingsIntent)
        }
    }
}

