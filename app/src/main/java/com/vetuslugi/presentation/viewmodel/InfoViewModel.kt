package com.vetuslugi.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.vetuslugi.domain.model.Place
import com.vetuslugi.domain.usecase.place.UpdateClubUseCase
import com.vetuslugi.domain.usecase.place.UpdateNurseryUseCase
import com.vetuslugi.domain.usecase.place.UpdateShelterUseCase
import com.vetuslugi.presentation.viewmodel.SharedPlaceViewModel.PlaceType
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class InfoViewModel(
    private val updateShelterUseCase: UpdateShelterUseCase,
    private val updateNurseryUseCase: UpdateNurseryUseCase,
    private val updateClubUseCase: UpdateClubUseCase
) : ViewModel() {

    sealed class UiState {
        object Idle : UiState()
        object Loading : UiState()
        data class Success(val fromProfile: Boolean) : UiState()
        data class Error(val message: String) : UiState()
    }

    private val _uiState = MutableStateFlow<UiState>(UiState.Idle)
    val uiState: StateFlow<UiState> = _uiState

    fun updatePlace(place: Place, type: PlaceType, fromProfile: Boolean) {
        viewModelScope.launch {
            _uiState.value = UiState.Loading
            val result = when (type) {
                PlaceType.SHELTER -> updateShelterUseCase(place)
                PlaceType.NURSERY -> updateNurseryUseCase(place)
                PlaceType.CLUB -> updateClubUseCase(place)
            }
            result
                .onSuccess { _uiState.value = UiState.Success(fromProfile) }
                .onFailure { _uiState.value = UiState.Error("Ошибка сохранения") }
        }
    }

    fun resetState() {
        _uiState.value = UiState.Idle
    }
}
