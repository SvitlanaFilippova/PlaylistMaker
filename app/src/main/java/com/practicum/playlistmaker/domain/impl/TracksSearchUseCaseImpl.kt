package com.practicum.playlistmaker.domain.impl

import com.practicum.playlistmaker.domain.api.TracksRepository
import com.practicum.playlistmaker.domain.api.TracksSearchUseCase
import java.util.concurrent.Executors

class TracksSearchUseCaseImpl(private val repository: TracksRepository) : TracksSearchUseCase {

    override fun execute(expression: String, consumer: TracksSearchUseCase.TracksConsumer) {
        val executor = Executors.newCachedThreadPool()
        executor.execute {

            try {
                val tracks = repository.searchTracks(expression)
                if (repository.resultCode == 200 && tracks.isNotEmpty()) {
                    consumer.consume(tracks)
                } else {
                    consumer.onError(repository.resultCode)
                }
            } catch (e: Exception) {
                consumer.onError(repository.resultCode)
            }


        }
    }
}

