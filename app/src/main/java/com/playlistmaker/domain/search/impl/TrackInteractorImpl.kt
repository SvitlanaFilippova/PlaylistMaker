package com.playlistmaker.domain.search.impl

import com.playlistmaker.domain.search.TrackInteractor
import com.playlistmaker.domain.search.TracksRepository
import com.playlistmaker.util.Resource
import java.util.concurrent.Executors

class TrackInteractorImpl(private val repository: TracksRepository) : TrackInteractor {
    private val executor = Executors.newCachedThreadPool()

    override fun search(expression: String, consumer: TrackInteractor.TracksConsumer) {
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





