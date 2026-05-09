package com.vetuslugi.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.vetuslugi.domain.model.Animal
import com.vetuslugi.domain.usecase.animal.GetAnimalsByNurseryUseCase
import com.vetuslugi.domain.usecase.animal.GetAnimalsByShelterUseCase
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.launch

class AnimalsViewModel(
    private val getAnimalsByShelterUseCase: GetAnimalsByShelterUseCase,
    private val getAnimalsByNurseryUseCase: GetAnimalsByNurseryUseCase
) : ViewModel() {

    private var originalAnimals: List<Animal> = emptyList()

    val searchQuery = MutableStateFlow("")

    private val _animals = MutableStateFlow<List<Animal>>(emptyList())
    val animals: StateFlow<List<Animal>> = _animals

    private val _loading = MutableStateFlow(false)
    val loading: StateFlow<Boolean> = _loading

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

    init {
        observeSearch()
    }

    @OptIn(FlowPreview::class)
    private fun observeSearch() {
        viewModelScope.launch {
            searchQuery
                .debounce(300)
                .distinctUntilChanged()
                .collectLatest { query ->
                    _animals.value = filter(originalAnimals, query)
                }
        }
    }

    private fun filter(list: List<Animal>, query: String) =
        if (query.isEmpty()) list
        else list.filter {
            it.nickname.orEmpty().contains(query, ignoreCase = true) ||
            it.species.orEmpty().contains(query, ignoreCase = true) ||
            it.breed.orEmpty().contains(query, ignoreCase = true)
        }

    fun loadAnimals(address: String, isNursery: Boolean) {
        viewModelScope.launch {
            _loading.value = true
            _error.value = null
            val result = if (isNursery) getAnimalsByNurseryUseCase(address)
                         else getAnimalsByShelterUseCase(address)
            result
                .onSuccess {
                    originalAnimals = it
                    _animals.value = filter(it, searchQuery.value)
                }
                .onFailure { e ->
                    _error.value = "Ошибка загрузки: ${e.message}"
                }
            _loading.value = false
        }
    }

    fun clearError() {
        _error.value = null
    }
}
