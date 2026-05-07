package com.vetuslugi.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.vetuslugi.data.local.UserSession
import com.vetuslugi.domain.model.User
import com.vetuslugi.domain.usecase.auth.UpdateUserUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class EditProfileViewModel(
    private val updateUserUseCase: UpdateUserUseCase,
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

    fun getCurrentUser(): User? = userSession.getUser()

    fun updateUser(name: String, surname: String, phone: String, passwordInput: String) {
        val currentUser = userSession.getUser() ?: return
        if (passwordInput != currentUser.password) {
            _uiState.value = UiState.Error("Неверный пароль")
            return
        }
        viewModelScope.launch {
            _uiState.value = UiState.Loading
            val updated = currentUser.copy(name = name, surname = surname, phone = phone)
            updateUserUseCase(updated)
                .onSuccess {
                    userSession.saveUser(updated)
                    _uiState.value = UiState.Success
                }
                .onFailure { _uiState.value = UiState.Error("Ошибка обновления данных") }
        }
    }

    fun resetState() {
        _uiState.value = UiState.Idle
    }
}
