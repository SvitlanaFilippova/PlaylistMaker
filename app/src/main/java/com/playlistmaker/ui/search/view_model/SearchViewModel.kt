package com.playlistmaker.ui.search.view_model


import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.playlistmaker.domain.Track
import com.playlistmaker.domain.search.HistoryInteractor
import com.playlistmaker.domain.search.TrackInteractor


class SearchViewModel(
    private val historyInteractor: HistoryInteractor,
    private val tracksInteractor: TrackInteractor
) : ViewModel() {
    private var searchState = MutableLiveData<SearchScreenState>(SearchScreenState.Empty)
    fun getSearchState(): LiveData<SearchScreenState> = mediatorStateLiveData

    private val showPlayerTrigger = MutableLiveData<Track>()
    fun getPlayerTrigger(): LiveData<Track> = showPlayerTrigger

    private val mediatorStateLiveData = MediatorLiveData<SearchScreenState>().also { liveData ->

        liveData.addSource(searchState) { state ->
            liveData.value = when (state) {
                is SearchScreenState.SearchResults -> {
                    val newTrackList =
                        state.tracks.sortedByDescending { it.inFavorite }.toCollection(ArrayList())
                    SearchScreenState.SearchResults(newTrackList)
                }

                else -> state
            }
        }
    }

    private var prevExpression = ""

    fun startSearch(expression: String) {
        val actualSearchResults = getActualSearchResults(expression)
        if (!actualSearchResults.isNullOrEmpty()) {
            searchState.postValue(SearchScreenState.SearchResults(actualSearchResults))
            return
        } else {
            searchState.postValue(SearchScreenState.Loading)
            tracksInteractor.search(expression, object : TrackInteractor.TracksConsumer {
                //Выполнение происходит в другом потоке
                override fun consume(foundTracks: ArrayList<Track>?, errorMessage: String?) {

                    if (!foundTracks.isNullOrEmpty()) {
                        searchState.postValue(SearchScreenState.SearchResults(foundTracks))

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
            return (searchState.value as SearchScreenState.SearchResults).tracks
        } else {
            prevExpression = newExpression
            return null
        }
    }

    fun setStateHistory() {
        val sharedPrefsHistory = historyInteractor.read() as ArrayList<Track>
        if (sharedPrefsHistory.isNotEmpty()) {
            searchState.value = SearchScreenState.History(sharedPrefsHistory)

        } else searchState.value = SearchScreenState.Empty
    }


    fun onTrackClick(track: Track) {
        showProductDetails(track)
        updateHistory(track)
    }

    private fun showProductDetails(track: Track) {
        showPlayerTrigger.value = track
    }

    fun clearHistory() {
        historyInteractor.clear()
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
            searchState.value = SearchScreenState.History(sharedPrefsHistory)
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