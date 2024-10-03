package com.practicum.playlistmaker.presentation

import android.content.Context
import androidx.core.view.isVisible
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.presentation.VIews.PlaceholderViews
import com.practicum.playlistmaker.presentation.interfaces.PlaceholderManager


class PlaceholderManagerImpl(val context: Context, val placeholderViews: PlaceholderViews) :
    PlaceholderManager {
    private var placeholderIsVisible = false

    override fun execute(status: PlaceholderManager.PlaceholderStatus): Boolean {
        when (status) {
            PlaceholderManager.PlaceholderStatus.NOTHING_FOUND -> {
                placeholderViews.searchLlPlaceholder.isVisible = true
                placeholderViews.searchTvPlaceholderExtraMessage.isVisible = false
                placeholderViews.searchBvPlaceholderButton.isVisible = false

                placeholderViews.searchIvPlaceholderImage.apply {
                    setImageResource(R.drawable.ic_nothing_found)
                    isVisible = true
                }
                placeholderViews.searchTvPlaceholderMessage.apply {
                    isVisible = true
                    text = context.getString(R.string.search_error_nothing_found)
                }
                placeholderIsVisible = true
            }


            PlaceholderManager.PlaceholderStatus.NO_NETWORK -> {
                placeholderViews.searchLlPlaceholder.isVisible = true
                placeholderViews.searchBvPlaceholderButton.isVisible = true

                placeholderViews.searchIvPlaceholderImage.apply {
                    isVisible = true
                    setImageResource(R.drawable.ic_no_internet)
                }
                placeholderViews.searchTvPlaceholderMessage.apply {
                    isVisible = true
                    text = context.getString(R.string.search_error_network)
                }
                placeholderViews.searchTvPlaceholderExtraMessage.apply {
                    isVisible = true
                    text =
                        context.getString(R.string.search_error_network_extra)
                }
                placeholderIsVisible = true
            }

            PlaceholderManager.PlaceholderStatus.HIDDEN -> {
                placeholderViews.searchLlPlaceholder.isVisible = false
                placeholderViews.searchIvPlaceholderImage.isVisible = false
                placeholderViews.searchTvPlaceholderMessage.isVisible = false
                placeholderViews.searchTvPlaceholderExtraMessage.isVisible = false
                placeholderViews.searchBvPlaceholderButton.isVisible = false
                placeholderIsVisible = false
            }
        }
        return placeholderIsVisible
    }


}