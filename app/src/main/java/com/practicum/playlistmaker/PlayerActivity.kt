package com.practicum.playlistmaker

import android.icu.text.SimpleDateFormat
import android.media.MediaPlayer
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.widget.ImageButton
import androidx.core.view.isVisible
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.google.gson.Gson
import com.practicum.playlistmaker.databinding.ActivityPlayerBinding
import java.time.LocalDateTime
import java.util.Locale


class PlayerActivity() : AppCompatActivity() {
    private lateinit var binding: ActivityPlayerBinding
    private var mediaPlayer = MediaPlayer()
    private lateinit var buttonPlay: ImageButton
    private lateinit var url: String
    private var mainThreadHandler: Handler? = null
    private var trackTimeProgress = formatTime(0L)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val track = Gson().fromJson(intent.getStringExtra("track"), Track::class.java)
        binding = ActivityPlayerBinding.inflate(layoutInflater)
        setContentView(binding.root)

        mainThreadHandler = Handler(Looper.getMainLooper())
        binding.apply {

            ibArrowBack.setOnClickListener() { finish() }
            buttonPlay = ibPlay
            buttonPlay.isEnabled = false
            url = track.previewUrl
            preparePlayer()

            tvTrackProgress.text = trackTimeProgress
            tvTrackName.text = track.trackName
            tvArtistName.text = track.artistName
            tvDurationTrack.text =
                SimpleDateFormat("mm:ss", Locale.getDefault()).format(track.trackTimeMillis)
            tvYearTrack.text = track.releaseDate.slice(0..3)

            tvGenreTrack.text = track.primaryGenreName
            tvCountryTrack.text = track.country
            Glide.with(applicationContext)
                .load(track.getCoverArtwork())
                .centerCrop()
                .transform(
                    RoundedCorners(
                        resources.getDimensionPixelSize(R.dimen.player_cover_radius_8)
                    )
                )
                .placeholder(R.drawable.ic_big_placeholder)
                .into(binding.ivCover)

            if (track.collectionName.isNotEmpty())
                tvCollectionTrack.text = track.collectionName
            else {
                tvCollectionTrack.isVisible = false
                tvCollectionTitle.isVisible = false
            }

            buttonPlay.setOnClickListener {
                playbackControl()
            }
        }


    }

    fun preparePlayer() {
        mediaPlayer.setDataSource(url)
        mediaPlayer.prepareAsync()
        mediaPlayer.setOnPreparedListener {
            buttonPlay.isEnabled = true
            playerState = STATE_PREPARED
        }
        mediaPlayer.setOnCompletionListener {
            buttonPlay.setImageResource(R.drawable.ic_play)
            playerState = STATE_PREPARED
        }
    }

    private fun startPlayer() {
        mediaPlayer.start()
        buttonPlay.setImageResource(R.drawable.ic_pause)
        playerState = STATE_PLAYING
    }

    private fun pausePlayer() {
        mediaPlayer.pause()
        buttonPlay.setImageResource(R.drawable.ic_play)
        playerState = STATE_PAUSED
    }

    private fun playbackControl() {
        when (playerState) {
            STATE_PLAYING -> {
                pausePlayer()
            }

            STATE_PREPARED, STATE_PAUSED -> {
                startPlayer()
            }
        }
    }

    private fun formatTime(time: Long): String {
        return SimpleDateFormat("mm:ss", Locale.getDefault()).format(time)
    }

    override fun onPause() {
        super.onPause()
        pausePlayer()
    }

    override fun onDestroy() {
        super.onDestroy()
        mediaPlayer.release()
    }

    companion object {
        private const val STATE_DEFAULT = 0
        private const val STATE_PREPARED = 1
        private const val STATE_PLAYING = 2
        private const val STATE_PAUSED = 3
    }

    private var playerState = STATE_DEFAULT
}