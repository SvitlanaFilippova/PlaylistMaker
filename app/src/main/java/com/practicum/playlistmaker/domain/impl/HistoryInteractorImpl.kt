package com.practicum.playlistmaker.domain.impl

import com.practicum.playlistmaker.domain.api.HistoryInteractor
import com.practicum.playlistmaker.domain.api.HistoryRepository
import com.practicum.playlistmaker.domain.models.Track

class HistoryInteractorImpl(private val repository: HistoryRepository) : HistoryInteractor {


    override fun clear() {
        repository.clear()
    }

    override fun read(): ArrayList<Track> {
        return repository.read()
    }

    override fun save(history: ArrayList<Track>) {
        repository.save(history)
    }


}