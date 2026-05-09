package com.vetuslugi.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.vetuslugi.domain.model.Place
import com.vetuslugi.domain.usecase.place.GetClubsUseCase
import com.vetuslugi.domain.usecase.place.GetNurseriesUseCase
import com.vetuslugi.domain.usecase.place.GetSheltersUseCase
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.launch

class PlacesViewModel(
    private val getSheltersUseCase: GetSheltersUseCase,
    private val getNurseriesUseCase: GetNurseriesUseCase,
    private val getClubsUseCase: GetClubsUseCase
) : ViewModel() {

    enum class Tab { SHELTERS, NURSERIES, CLUBS }

    private var originalShelters: List<Place> = emptyList()
    private var originalNurseries: List<Place> = emptyList()
    private var originalClubs: List<Place> = emptyList()

    val searchQuery = MutableStateFlow("")
    val activeTab = MutableStateFlow(Tab.SHELTERS)

    private val _shelters = MutableStateFlow<List<Place>>(emptyList())
    val shelters: StateFlow<List<Place>> = _shelters

    private val _nurseries = MutableStateFlow<List<Place>>(emptyList())
    val nurseries: StateFlow<List<Place>> = _nurseries

    private val _clubs = MutableStateFlow<List<Place>>(emptyList())
    val clubs: StateFlow<List<Place>> = _clubs

    private val _loading = MutableStateFlow(false)
    val loading: StateFlow<Boolean> = _loading

    init {
        loadAll()
        observeSearch()
    }

    fun loadAll() {
        viewModelScope.launch { loadShelters() }
        viewModelScope.launch { loadNurseries() }
        viewModelScope.launch { loadClubs() }
    }

    @OptIn(FlowPreview::class)
    private fun observeSearch() {
        viewModelScope.launch {
            searchQuery
                .debounce(300)
                .distinctUntilChanged()
                .collectLatest { query ->
                    _shelters.value = filter(originalShelters, query)
                    _nurseries.value = filter(originalNurseries, query)
                    _clubs.value = filter(originalClubs, query)
                }
        }
    }

    private fun filter(list: List<Place>, query: String) =
        if (query.isEmpty()) list else list.filter { it.name.contains(query, ignoreCase = true) }

    private suspend fun loadShelters() {
        getSheltersUseCase().onSuccess {
            originalShelters = it
            _shelters.value = filter(it, searchQuery.value)
        }
    }

    private suspend fun loadNurseries() {
        getNurseriesUseCase().onSuccess {
            originalNurseries = it
            _nurseries.value = filter(it, searchQuery.value)
        }
    }

    private suspend fun loadClubs() {
        getClubsUseCase().onSuccess {
            originalClubs = it
            _clubs.value = filter(it, searchQuery.value)
        }
    }
}
