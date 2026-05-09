package com.vetuslugi.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.vetuslugi.data.local.UserSession
import com.vetuslugi.domain.model.Place
import com.vetuslugi.domain.usecase.place.AddClubUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class AddClubViewModel(
    private val addClubUseCase: AddClubUseCase,
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

    fun addClub(name: String, address: String, phone: String, description: String) {
        val owner = userSession.getLogin() ?: return
        val club = Place(address, name, phone, description, owner)
        viewModelScope.launch {
            _uiState.value = UiState.Loading
            addClubUseCase(club)
                .onSuccess { _uiState.value = UiState.Success }
                .onFailure { _uiState.value = UiState.Error("Ошибка создания клуба") }
        }
    }

    fun resetState() {
        _uiState.value = UiState.Idle
    }
}
