package com.vetuslugi.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.vetuslugi.domain.model.User
import com.vetuslugi.domain.usecase.auth.RegisterUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class RegisterViewModel(
    private val registerUseCase: RegisterUseCase
) : ViewModel() {

    sealed class UiState {
        object Idle : UiState()
        object Loading : UiState()
        object Success : UiState()
        data class Error(val message: String) : UiState()
    }

    private val _uiState = MutableStateFlow<UiState>(UiState.Idle)
    val uiState: StateFlow<UiState> = _uiState

    fun register(name: String, surname: String, phone: String, login: String, password: String, role: String) {
        viewModelScope.launch {
            _uiState.value = UiState.Loading
            registerUseCase(User(login, password, name, surname, phone, role))
                .onSuccess { _uiState.value = UiState.Success }
                .onFailure { _uiState.value = UiState.Error("Ошибка создания учётной записи") }
        }
    }

    fun resetState() {
        _uiState.value = UiState.Idle
    }
}
