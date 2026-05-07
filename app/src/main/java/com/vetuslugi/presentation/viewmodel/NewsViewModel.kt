package com.vetuslugi.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.vetuslugi.data.local.SearchHistoryManager
import com.vetuslugi.domain.model.News
import com.vetuslugi.domain.usecase.news.GetNewsUseCase
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.launch

class NewsViewModel(
    private val getNewsUseCase: GetNewsUseCase,
    private val searchHistoryManager: SearchHistoryManager
) : ViewModel() {

    sealed class UiState {
        object Loading : UiState()
        object Success : UiState()
        object Error : UiState()
    }

    private val _uiState = MutableStateFlow<UiState>(UiState.Loading)
    val uiState: StateFlow<UiState> = _uiState

    private var originalNews: List<News> = emptyList()

    private val _filteredNews = MutableStateFlow<List<News>>(emptyList())
    val filteredNews: StateFlow<List<News>> = _filteredNews

    val searchQuery = MutableStateFlow("")

    init {
        loadNews()
        observeSearch()
    }

    @OptIn(FlowPreview::class)
    private fun observeSearch() {
        viewModelScope.launch {
            searchQuery
                .debounce(300)
                .distinctUntilChanged()
                .collectLatest { query -> applyFilter(query) }
        }
    }

    fun loadNews() {
        viewModelScope.launch {
            _uiState.value = UiState.Loading
            getNewsUseCase()
                .onSuccess { news ->
                    originalNews = news
                    applyFilter(searchQuery.value)
                    _uiState.value = UiState.Success
                }
                .onFailure { _uiState.value = UiState.Error }
        }
    }

    private fun applyFilter(query: String) {
        val filtered = if (query.isEmpty()) originalNews
        else originalNews.filter { it.title.contains(query, ignoreCase = true) }
        _filteredNews.value = filtered
        if (query.isNotEmpty()) searchHistoryManager.addQuery(query)
    }

    fun getSearchHistory(): List<String> = searchHistoryManager.load()

    fun clearSearchHistory() = searchHistoryManager.clear()
}
