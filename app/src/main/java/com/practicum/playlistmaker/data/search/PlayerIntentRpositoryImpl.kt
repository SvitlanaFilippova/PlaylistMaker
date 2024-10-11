package com.practicum.playlistmaker.data.search

import android.content.Context
import android.content.Intent
import com.google.gson.Gson
import com.practicum.playlistmaker.domain.Track
import com.practicum.playlistmaker.domain.sharing.IntentRepository
import com.practicum.playlistmaker.ui.player.PlayerActivity

class PlayerIntentRepositoryImpl(private val context: Context, private val track: Track) :
    IntentRepository {
    override fun getIntent(): Intent {
        val playerIntent = Intent(
            Intent(context, PlayerActivity::class.java)
        )

        return playerIntent.apply {
            putExtra("track", Gson().toJson(track))
            flags = Intent.FLAG_ACTIVITY_NEW_TASK
        }
    }
}

