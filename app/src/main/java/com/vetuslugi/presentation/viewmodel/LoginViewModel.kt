package com.vetuslugi.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.vetuslugi.data.local.UserSession
import com.vetuslugi.domain.usecase.auth.GetUserUseCase
import com.vetuslugi.domain.usecase.auth.LoginUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class LoginViewModel(
    private val loginUseCase: LoginUseCase,
    private val getUserUseCase: GetUserUseCase,
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

    fun login(login: String, password: String) {
        viewModelScope.launch {
            _uiState.value = UiState.Loading
            loginUseCase(login, password)
                .onSuccess {
                    getUserUseCase(login)
                        .onSuccess { user ->
                            userSession.saveUser(user)
                            _uiState.value = UiState.Success
                        }
                        .onFailure { _uiState.value = UiState.Error("Ошибка получения данных") }
                }
                .onFailure { _uiState.value = UiState.Error("Ошибка входа") }
        }
    }

    fun resetState() {
        _uiState.value = UiState.Idle
    }
}
