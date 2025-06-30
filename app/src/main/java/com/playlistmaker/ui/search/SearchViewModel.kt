package com.playlistmaker.ui.search


import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.playlistmaker.domain.models.Track
import com.playlistmaker.domain.search.HistoryInteractor
import com.playlistmaker.domain.search.TrackInteractor
import com.playlistmaker.ui.util.debounce
import kotlinx.coroutines.launch


class SearchViewModel(
    private val historyInteractor: HistoryInteractor,
    private val tracksInteractor: TrackInteractor
) : ViewModel() {
    private var latestSearchText = ""


    private var searchState = MutableLiveData<SearchScreenState>(SearchScreenState.Empty)
    fun getSearchState(): LiveData<SearchScreenState> = searchState


    private val trackSearchDebounce = debounce<String>(
        SEARCH_DEBOUNCE_DELAY,
        viewModelScope,
        useLastParam = true
    ) { expression -> startSearch(expression) }


    fun searchWithDebounce(changedText: String) {
        val actualSearchResults = getActualSearchResults(changedText)
        if (!actualSearchResults.isNullOrEmpty()) {
            searchState.postValue(SearchScreenState.SearchResults(actualSearchResults))
            return
        } else trackSearchDebounce.invoke(changedText)
    }


    fun cancelSearchDebounce() {
        trackSearchDebounce.cancel()
    }


    fun startSearch(expression: String) {
        searchState.postValue(SearchScreenState.Loading)
        viewModelScope.launch {
            tracksInteractor.searchTracks(expression).collect { pair ->
                processSearchResult(pair.first, pair.second)
            }
        }
    }

    private fun processSearchResult(foundTracks: ArrayList<Track>?, errorMessage: String?) {

        if (!foundTracks.isNullOrEmpty()) {
            searchState.postValue(SearchScreenState.SearchResults(foundTracks))

        } else if (foundTracks != null && foundTracks.isEmpty()) {
            searchState.postValue(SearchScreenState.NothingFound)
        }
        if (errorMessage != null) {
            searchState.postValue(SearchScreenState.NetworkError)
        }
    }


    private fun getActualSearchResults(changedText: String): ArrayList<Track>? {
        if ((latestSearchText == changedText) && (searchState.value is
                    SearchScreenState.SearchResults)
        ) {
            return (searchState.value as SearchScreenState.SearchResults).tracks
        } else {
            latestSearchText = changedText
            return null
        }
    }

    fun setStateHistory() {
        val sharedPrefsHistory = historyInteractor.read() as ArrayList<Track>
        if (sharedPrefsHistory.isNotEmpty()) {
            searchState.value = SearchScreenState.History(sharedPrefsHistory)

        } else searchState.value = SearchScreenState.Empty
    }


    fun clearHistory() {
        historyInteractor.clear()
        searchState.value = SearchScreenState.Empty
    }


    fun updateHistory(track: Track) {
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

    private companion object {
        private const val SEARCH_DEBOUNCE_DELAY = 2000L
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