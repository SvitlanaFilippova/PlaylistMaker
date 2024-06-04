package com.practicum.playlistmaker

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

        val buttonSearchClickListener: View.OnClickListener = object : View.OnClickListener { override fun onClick(v: View?) {
            Toast.makeText(this@MainActivity, "Нажали на кнопку \"Поиск\"", Toast.LENGTH_SHORT).show()

        } }
        buttonSearch.setOnClickListener(buttonSearchClickListener)

        buttonLibrary.setOnClickListener {
            Toast.makeText(this@MainActivity, "Нажали на кнопку \"Медиатека\"", Toast.LENGTH_SHORT).show()
        }
        buttonSettings.setOnClickListener {
            Toast.makeText(this@MainActivity, "Нажали на кнопку \"Настройки\"", Toast.LENGTH_SHORT).show()
        }
    }
}