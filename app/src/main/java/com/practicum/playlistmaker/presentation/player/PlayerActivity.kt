package com.practicum.playlistmaker.presentation.player

import android.media.MediaPlayer
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.ImageButton
import androidx.appcompat.app.AppCompatActivity
import com.google.gson.Gson
import com.practicum.playlistmaker.Creator
import com.practicum.playlistmaker.databinding.ActivityPlayerBinding
import com.practicum.playlistmaker.domain.api.PlayerInteractor
import com.practicum.playlistmaker.domain.models.Track


class PlayerActivity() : AppCompatActivity() {
    private lateinit var binding: ActivityPlayerBinding
    private var mediaPlayer = MediaPlayer()
    private lateinit var buttonPlay: ImageButton
    private var mainThreadHandler: Handler? = null
    private lateinit var playerInteractor: PlayerInteractor


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val track = Gson().fromJson(intent.getStringExtra("track"), Track::class.java)
        binding = ActivityPlayerBinding.inflate(layoutInflater)
        setContentView(binding.root)

        Creator.init(applicationContext, binding)
        mainThreadHandler = Handler(Looper.getMainLooper())
        playerInteractor =
            Creator.providePlayerInteractor(mediaPlayer, mainThreadHandler, track)

        binding.ibArrowBack.setOnClickListener { finish() }

        binding.apply {
            buttonPlay = ibPlay
            buttonPlay.isEnabled = false
        }
        playerInteractor.prepare()

        mediaPlayer.setOnPreparedListener {
            playerState = playerInteractor.setOnPreparedListener()
        }

        mediaPlayer.setOnCompletionListener {
            playerState = playerInteractor.setOnCompletionListener()
        }

        buttonPlay.setOnClickListener {
            playerState = playerInteractor.playbackControl(playerState)

        }
        Creator.providePlayerTrackDataUpdater().execute(track)
    }


    override fun onPause() {
        super.onPause()
        playerState = playerInteractor.pause()
    }

    override fun onDestroy() {
        super.onDestroy()
        playerInteractor.stopRefreshingProgress()
        mediaPlayer.release()
    }

    companion object {
        const val STATE_DEFAULT = 0
        const val STATE_PREPARED = 1
        const val STATE_PLAYING = 2
        const val STATE_PAUSED = 3
        const val PROGRESS_REFRESH_DELAY_MILLIS = 400L
    }

    private var playerState = STATE_DEFAULT
}