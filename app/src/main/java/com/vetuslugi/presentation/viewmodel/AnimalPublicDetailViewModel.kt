package com.vetuslugi.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.vetuslugi.domain.model.Place
import com.vetuslugi.domain.usecase.place.GetNurseriesUseCase
import com.vetuslugi.domain.usecase.place.GetSheltersUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class AnimalPublicDetailViewModel(
    private val getSheltersUseCase: GetSheltersUseCase,
    private val getNurseriesUseCase: GetNurseriesUseCase
) : ViewModel() {

    private val _place = MutableStateFlow<Place?>(null)
    val place: StateFlow<Place?> = _place

    private val _isNursery = MutableStateFlow(false)
    val isNursery: StateFlow<Boolean> = _isNursery

    fun loadPlace(address: String, isNursery: Boolean) {
        _isNursery.value = isNursery
        viewModelScope.launch {
            val result = if (isNursery) getNurseriesUseCase() else getSheltersUseCase()
            result.onSuccess { places ->
                _place.value = places.find { it.address == address }
            }
        }
    }
}
