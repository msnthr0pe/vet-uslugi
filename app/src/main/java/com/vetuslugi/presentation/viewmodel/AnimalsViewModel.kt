package com.vetuslugi.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.vetuslugi.domain.model.Animal
import com.vetuslugi.domain.usecase.animal.GetAnimalsByNurseryStreamUseCase
import com.vetuslugi.domain.usecase.animal.GetAnimalsByNurseryUseCase
import com.vetuslugi.domain.usecase.animal.GetAnimalsByShelterStreamUseCase
import com.vetuslugi.domain.usecase.animal.GetAnimalsByShelterUseCase
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.launch

class AnimalsViewModel(
    private val getAnimalsByShelterStreamUseCase: GetAnimalsByShelterStreamUseCase,
    private val getAnimalsByNurseryStreamUseCase: GetAnimalsByNurseryStreamUseCase,
    private val getAnimalsByShelterUseCase: GetAnimalsByShelterUseCase,
    private val getAnimalsByNurseryUseCase: GetAnimalsByNurseryUseCase,
) : ViewModel() {

    private var originalAnimals: List<Animal> = emptyList()

    val searchQuery = MutableStateFlow("")

    private val _animals = MutableStateFlow<List<Animal>>(emptyList())
    val animals: StateFlow<List<Animal>> = _animals

    private val _loading = MutableStateFlow(false)
    val loading: StateFlow<Boolean> = _loading

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

    private var observingJob: Job? = null

    init {
        observeSearch()
    }

    fun startObserving(address: String, isNursery: Boolean) {
        observingJob?.cancel()
        observingJob = viewModelScope.launch {
            val flow = if (isNursery) getAnimalsByNurseryStreamUseCase(address)
                       else getAnimalsByShelterStreamUseCase(address)
            flow.collect { animals ->
                originalAnimals = animals
                _animals.value = filter(animals, searchQuery.value)
            }
        }
    }

    fun syncFromApi(address: String, isNursery: Boolean) {
        viewModelScope.launch {
            _loading.value = true
            val result = if (isNursery) getAnimalsByNurseryUseCase(address)
                         else getAnimalsByShelterUseCase(address)
            result.onFailure { _error.value = "Ошибка загрузки: ${it.message}" }
            _loading.value = false
        }
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

    fun clearError() {
        _error.value = null
    }
}
