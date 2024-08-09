package com.practicum.playlistmaker

import android.icu.text.SimpleDateFormat
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.core.view.isVisible
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.google.gson.Gson
import com.practicum.playlistmaker.databinding.ActivityPlayerBinding
import java.time.LocalDateTime
import java.util.Locale


class PlayerActivity() : AppCompatActivity() {
    private lateinit var binding: ActivityPlayerBinding


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val track = Gson().fromJson(intent.getStringExtra("track"), Track::class.java)
        binding = ActivityPlayerBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.apply {

            ibArrowBack.setOnClickListener() { finish() }
            tvTrackName.text = track.trackName
            tvArtistName.text = track.artistName
            tvDurationTrack.text =
                SimpleDateFormat("mm:ss", Locale.getDefault()).format(track.trackTimeMillis)
            tvYearTrack.text = "1991" // TODO как преобразовать track.releaseDate в год?
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
        }
    }
}