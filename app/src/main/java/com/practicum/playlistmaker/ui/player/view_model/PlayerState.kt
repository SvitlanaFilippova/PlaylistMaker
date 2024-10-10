package com.practicum.playlistmaker.ui.player.view_model

sealed class PlayerState {
    data object Default : PlayerState()
    data object Prepared : PlayerState()
    data object Playing : PlayerState()
    data object Paused : PlayerState()
}