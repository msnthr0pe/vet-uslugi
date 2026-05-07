package com.vetuslugi.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.vetuslugi.domain.model.Place
import com.vetuslugi.domain.usecase.place.GetSheltersUseCase
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.launch

class SheltersViewModel(
    private val getSheltersUseCase: GetSheltersUseCase
) : ViewModel() {

    private var originalShelters: List<Place> = emptyList()

    private val _shelters = MutableStateFlow<List<Place>>(emptyList())
    val shelters: StateFlow<List<Place>> = _shelters

    val searchQuery = MutableStateFlow("")

    init {
        loadShelters()
        observeSearch()
    }

    @OptIn(FlowPreview::class)
    private fun observeSearch() {
        viewModelScope.launch {
            searchQuery
                .debounce(300)
                .distinctUntilChanged()
                .collectLatest { query ->
                    _shelters.value = if (query.isEmpty()) originalShelters
                    else originalShelters.filter { it.name.contains(query, ignoreCase = true) }
                }
        }
    }

    fun loadShelters() {
        viewModelScope.launch {
            getSheltersUseCase()
                .onSuccess { shelters ->
                    originalShelters = shelters
                    _shelters.value = shelters
                }
        }
    }
}
