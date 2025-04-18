package com.playlistmaker.ui.library

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.playlistmaker.ui.library.favorites.FavoritesFragment
import com.playlistmaker.ui.library.playlists.YourPlaylistsFragment


class LibraryViewPagerAdapter(
    fragmentManager: FragmentManager,
    lifecycle: Lifecycle
) : FragmentStateAdapter(fragmentManager, lifecycle) {

    override fun getItemCount(): Int {
        return 2
    }

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> {
                FavoritesFragment.newInstance()
            }

            1 -> YourPlaylistsFragment.newInstance()

            else -> {
                FavoritesFragment.newInstance()
            }
        }
    }
}