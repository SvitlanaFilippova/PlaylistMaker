package com.playlistmaker.domain.search.impl

import com.playlistmaker.domain.models.Track
import com.playlistmaker.domain.search.HistoryInteractor
import com.playlistmaker.domain.search.HistoryRepository

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