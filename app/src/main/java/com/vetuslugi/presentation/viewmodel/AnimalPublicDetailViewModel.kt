package com.vetuslugi.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.vetuslugi.domain.model.Place
import com.vetuslugi.domain.usecase.place.GetNurseryByAddressUseCase
import com.vetuslugi.domain.usecase.place.GetShelterByAddressUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class AnimalPublicDetailViewModel(
    private val getShelterByAddressUseCase: GetShelterByAddressUseCase,
    private val getNurseryByAddressUseCase: GetNurseryByAddressUseCase
) : ViewModel() {

    private val _place = MutableStateFlow<Place?>(null)
    val place: StateFlow<Place?> = _place

    private val _placeLoading = MutableStateFlow(false)
    val placeLoading: StateFlow<Boolean> = _placeLoading

    private val _isNursery = MutableStateFlow(false)
    val isNursery: StateFlow<Boolean> = _isNursery

    fun loadPlace(address: String, isNursery: Boolean) {
        _isNursery.value = isNursery
        _placeLoading.value = true
        viewModelScope.launch {
            val result = if (isNursery) getNurseryByAddressUseCase(address)
                         else getShelterByAddressUseCase(address)
            _placeLoading.value = false
            result.onSuccess { _place.value = it }
        }
    }
}
