package com.practicum.playlistmaker.ui.player

import android.graphics.PorterDuff
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.activity.ComponentActivity
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.google.gson.Gson
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.creator.Creator
import com.practicum.playlistmaker.databinding.ActivityPlayerBinding
import com.practicum.playlistmaker.domain.Track
import com.practicum.playlistmaker.domain.player.PlayerInteractor


class PlayerActivity() : ComponentActivity() {

    private lateinit var binding: ActivityPlayerBinding
    private val mainThreadHandler: Handler by lazy { Handler(Looper.getMainLooper()) }
    private lateinit var playerInteractor: PlayerInteractor
    private var playerState = STATE_DEFAULT

    //    private lateinit var viewModel: TrackViewModel
    val track: Track by lazy { Gson().fromJson(intent.getStringExtra("track"), Track::class.java) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityPlayerBinding.inflate(layoutInflater)
        setContentView(binding.root)
//        viewModel = ViewModelProvider(this)[TrackViewModel::class.java]


        playerInteractor =
            Creator.providePlayerInteractor(mainThreadHandler)

        binding.ibArrowBack.setOnClickListener { finish() }
        binding.buttonPlay.isEnabled = false

        playerInteractor.prepare(track.previewUrl)

        playerInteractor.setOnPreparedListener { buttonEnabled, playerState ->
            binding.buttonPlay.isEnabled = buttonEnabled
            this.playerState = playerState
        }
        playerInteractor.setOnCompletionListener { trackProgressText, image, playerState ->
            binding.tvTrackProgress.text = trackProgressText
            binding.buttonPlay.setImageResource(image)
            this.playerState = playerState
        }

        binding.buttonPlay.setOnClickListener {
            playerState =
                playerInteractor.playbackControl(playerState,
                    { img -> binding.buttonPlay.setImageResource(img) },
                    { trackProgressText ->
                        binding.tvTrackProgress.text = trackProgressText
                    })

        }
        favoriteIconChanger(track.inFavorite)
        binding.ibAddToFavorite.setOnClickListener {
            favoriteSwitch()
        }

        // TODO + см тут: https://practicum.yandex.ru/learn/android-developer/courses/f9f11ab9-bbd7-4736-8091-f084176bf827/sprints/263922/topics/eb7982ae-f2b3-40c1-a9d4-c85270bdafb9/lessons/f8e34c4f-7326-4b41-bdf1-93f7b565172f/#:~:text=%D0%BF%D0%BB%D0%BE%D1%85%D0%BE%D0%B9%20%D0%BF%D0%BE%D0%BB%D1%8C%D0%B7%D0%BE%D0%B2%D0%B0%D1%82%D0%B5%D0%BB%D1%8C%D1%81%D0%BA%D0%B8%D0%B9%20%D0%BE%D0%BF%D1%8B%D1%82!-,%D0%94%D0%BE%D0%B1%D0%B0%D0%B2%D1%8C%D1%82%D0%B5%20%D0%B8%D0%B7%D0%BC%D0%B5%D0%BD%D0%B5%D0%BD%D0%B8%D1%8F,-%D1%82%D0%B5%D0%BA%D1%83%D1%89%D0%B5%D0%B3%D0%BE%20%D1%81%D0%BE%D1%81%D1%82%D0%BE%D1%8F%D0%BD%D0%B8%D1%8F%20%D1%8D%D0%BA%D1%80%D0%B0%D0%BD%D0%B0


        updateTrackData()
    }

    private fun favoriteSwitch() {
        if (track.inFavorite) {
            playerInteractor.removeTrackFromFavorites(track)
            track.inFavorite = false
        } else {
            playerInteractor.addTrackToFavorites(track)
            track.inFavorite = true
        }
        favoriteIconChanger(track.inFavorite)
    }

    private fun favoriteIconChanger(inFavorite: Boolean) {
        if (inFavorite) binding.ibAddToFavorite.setColorFilter(
            ContextCompat.getColor(
                this,
                R.color.player_ic_favorite
            ), PorterDuff.Mode.SRC_IN
        )
        else binding.ibAddToFavorite.setColorFilter(
            ContextCompat.getColor(
                this,
                R.color.white
            ), PorterDuff.Mode.SRC_IN
        )
    }

    //TODO пока что работает криво добавление в избранное

    private fun updateTrackData() {
        val context = applicationContext

        binding.apply {
            tvTrackProgress.text = context.getString(R.string.default_track_progress)
            tvTrackName.text = track.trackName
            tvArtistName.text = track.artistName
            tvDurationTrack.text = track.trackTime
            tvYearTrack.text = track.releaseDate.slice(0..3)
            tvGenreTrack.text = track.primaryGenreName
            tvCountryTrack.text = track.country

            Glide.with(context)
                .load(track.coverArtwork)
                .centerCrop()
                .transform(
                    RoundedCorners(
                        context.resources.getDimensionPixelSize(R.dimen.player_cover_radius_8)
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
        }
    }

    override fun onPause() {
        super.onPause()

        playerState = playerInteractor.pause { img -> binding.buttonPlay.setImageResource(img) }
    }

    override fun onDestroy() {
        super.onDestroy()
        playerInteractor.stopRefreshingProgress()
        playerInteractor.release()

    }

    companion object {
        const val STATE_DEFAULT = 0
        const val STATE_PREPARED = 1
        const val STATE_PLAYING = 2
        const val STATE_PAUSED = 3
        const val PROGRESS_REFRESH_DELAY_MILLIS = 400L
    }


}