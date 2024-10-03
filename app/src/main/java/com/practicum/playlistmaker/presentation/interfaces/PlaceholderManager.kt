package com.practicum.playlistmaker.presentation.interfaces


interface PlaceholderManager {
    fun execute(status: PlaceholderStatus): Boolean
    enum class PlaceholderStatus {
        NOTHING_FOUND,
        NO_NETWORK,
        HIDDEN
    }
}