package com.vetuslugi.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.vetuslugi.domain.model.Place
import com.vetuslugi.domain.usecase.place.GetNurseriesUseCase
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.launch

class NurseriesViewModel(
    private val getNurseriesUseCase: GetNurseriesUseCase
) : ViewModel() {

    private var originalNurseries: List<Place> = emptyList()

    private val _nurseries = MutableStateFlow<List<Place>>(emptyList())
    val nurseries: StateFlow<List<Place>> = _nurseries

    val searchQuery = MutableStateFlow("")

    init {
        loadNurseries()
        observeSearch()
    }

    @OptIn(FlowPreview::class)
    private fun observeSearch() {
        viewModelScope.launch {
            searchQuery
                .debounce(300)
                .distinctUntilChanged()
                .collectLatest { query ->
                    _nurseries.value = if (query.isEmpty()) originalNurseries
                    else originalNurseries.filter { it.name.contains(query, ignoreCase = true) }
                }
        }
    }

    fun loadNurseries() {
        viewModelScope.launch {
            getNurseriesUseCase()
                .onSuccess { nurseries ->
                    originalNurseries = nurseries
                    _nurseries.value = nurseries
                }
        }
    }
}
