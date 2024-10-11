package com.practicum.playlistmaker.ui.search.view_model


import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.practicum.playlistmaker.creator.Creator
import com.practicum.playlistmaker.domain.Track
import com.practicum.playlistmaker.domain.search.TrackInteractor


class SearchViewModel : ViewModel() {
    private var searchState = MutableLiveData<SearchScreenState>(SearchScreenState.Empty)
    fun getSearchState(): LiveData<SearchScreenState> = searchState
    private var prevExpression = ""
    private var trackListLiveData = MutableLiveData(ArrayList<Track>())
    fun getTrackListLiveData(): LiveData<ArrayList<Track>> = trackListLiveData


    private var historyInteractor = Creator.provideHistoryInteractor()

    fun startSearch(expression: String) {
        val actualSearchResults = getActualSearchResults(expression)
        if (!actualSearchResults.isNullOrEmpty()) {
            searchState.postValue(SearchScreenState.SearchResults(actualSearchResults))
            return
        } else {
            searchState.postValue(SearchScreenState.Loading)
            val tracksSearchUseCase = Creator.provideTracksSearchUseCase()
            tracksSearchUseCase.search(expression, object : TrackInteractor.TracksConsumer {
                //Выполнение происходит в другом потоке
                override fun consume(foundTracks: ArrayList<Track>?, errorMessage: String?) {

                    if (!foundTracks.isNullOrEmpty()) {
                        searchState.postValue(SearchScreenState.SearchResults(foundTracks))
                        trackListLiveData.postValue(foundTracks)
                    } else if (foundTracks != null && foundTracks.isEmpty()) {
                        searchState.postValue(SearchScreenState.NothingFound)
                    }
                    if (errorMessage != null) {
                        searchState.postValue(SearchScreenState.NetworkError)
                    }

                }
            })
        }
    }

    private fun getActualSearchResults(newExpression: String): ArrayList<Track>? {
        if ((prevExpression == newExpression) && (searchState.value is
                    SearchScreenState.SearchResults)
        ) {
            return trackListLiveData.value
        } else {
            prevExpression = newExpression
            return null
        }
    }

    fun setStateHistory() {
        val sharedPrefsHistory = historyInteractor.read() as ArrayList<Track>
        if (sharedPrefsHistory.isNotEmpty()) {
            searchState.value = SearchScreenState.History(sharedPrefsHistory)
            trackListLiveData.value = sharedPrefsHistory
        } else searchState.value = SearchScreenState.Empty
    }


    fun onTrackClick(track: Track) {
        startIntent(track)
        updateHistory(track)
    }

    private fun startIntent(track: Track) {
        Creator.providePlayerIntentUseCase(track).execute()
    }

    fun clearHistory() {
        historyInteractor.clear()
        if (searchState.value is SearchScreenState.History) {
            trackListLiveData.value = ArrayList()

        }
        searchState.value = SearchScreenState.Empty
    }


    private fun updateHistory(track: Track) {
        val sharedPrefsHistory = historyInteractor.read() as ArrayList<Track>
        val historyIsVisible = searchState.value is SearchScreenState.History
        sharedPrefsHistory.removeIf { it.trackId == track.trackId }
        if (sharedPrefsHistory.size > 9) {
            sharedPrefsHistory.removeAt(9)
        }
        sharedPrefsHistory.add(0, track)

        historyInteractor.save(sharedPrefsHistory)
        if (historyIsVisible) {
            trackListLiveData.value = sharedPrefsHistory
        }
    }
}


sealed interface SearchScreenState {
    data object Loading : SearchScreenState
    data object Empty : SearchScreenState
    data object NothingFound : SearchScreenState
    data object NetworkError : SearchScreenState
    data class SearchResults(var tracks: ArrayList<Track>) : SearchScreenState
    data class History(var tracks: ArrayList<Track>) : SearchScreenState
}