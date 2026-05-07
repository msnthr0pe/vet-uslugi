package com.vetuslugi.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.vetuslugi.data.local.UserSession
import com.vetuslugi.domain.model.Place
import com.vetuslugi.domain.usecase.place.AddNurseryUseCase
import com.vetuslugi.domain.usecase.place.AddShelterUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class AddPlaceViewModel(
    private val addShelterUseCase: AddShelterUseCase,
    private val addNurseryUseCase: AddNurseryUseCase,
    private val userSession: UserSession
) : ViewModel() {

    sealed class UiState {
        object Idle : UiState()
        object Loading : UiState()
        object Success : UiState()
        data class Error(val message: String) : UiState()
    }

    private val _uiState = MutableStateFlow<UiState>(UiState.Idle)
    val uiState: StateFlow<UiState> = _uiState

    fun addPlace(name: String, address: String, phone: String, description: String, isShelter: Boolean) {
        val owner = userSession.getLogin() ?: return
        val place = Place(address, name, phone, description, owner)
        viewModelScope.launch {
            _uiState.value = UiState.Loading
            val result = if (isShelter) addShelterUseCase(place) else addNurseryUseCase(place)
            result
                .onSuccess { _uiState.value = UiState.Success }
                .onFailure { _uiState.value = UiState.Error("Ошибка добавления места") }
        }
    }

    fun resetState() {
        _uiState.value = UiState.Idle
    }
}
