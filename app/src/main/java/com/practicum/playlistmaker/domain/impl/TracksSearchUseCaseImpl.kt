package com.practicum.playlistmaker.domain.impl

import com.practicum.playlistmaker.domain.api.TracksRepository
import com.practicum.playlistmaker.domain.api.TracksSearchUseCase
import com.practicum.playlistmaker.util.Resource
import java.util.concurrent.Executors

class TracksSearchUseCaseImpl(private val repository: TracksRepository) : TracksSearchUseCase {
    private val executor = Executors.newCachedThreadPool()

    override fun execute(expression: String, consumer: TracksSearchUseCase.TracksConsumer) {
        executor.execute {
            when (val resource = repository.searchTracks(expression)) {
                is Resource.Success -> {
                    consumer.consume(resource.data, null)
                }

                is Resource.Error -> {
                    consumer.consume(null, resource.message)
                }
            }
        }
    }
}





