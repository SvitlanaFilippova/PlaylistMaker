package com.practicum.playlistmaker.domain.search.impl

import com.practicum.playlistmaker.domain.Track
import com.practicum.playlistmaker.domain.search.HistoryInteractor
import com.practicum.playlistmaker.domain.search.HistoryRepository

class HistoryInteractorImpl(private val repository: HistoryRepository) : HistoryInteractor {


    override fun clear() {
        repository.clear()
    }

    override fun read(): List<Track> {
        return repository.read()
    }

    override fun save(history: List<Track>) {
        repository.save(history)
    }


}