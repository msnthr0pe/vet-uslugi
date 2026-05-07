package com.vetuslugi.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.vetuslugi.domain.model.News
import com.vetuslugi.domain.usecase.news.AddNewsUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class AddNewsViewModel(
    private val addNewsUseCase: AddNewsUseCase
) : ViewModel() {

    sealed class UiState {
        object Idle : UiState()
        object Loading : UiState()
        object Success : UiState()
        data class Error(val message: String) : UiState()
    }

    private val _uiState = MutableStateFlow<UiState>(UiState.Idle)
    val uiState: StateFlow<UiState> = _uiState

    fun addNews(title: String, description: String) {
        viewModelScope.launch {
            _uiState.value = UiState.Loading
            addNewsUseCase(News(title, description))
                .onSuccess { _uiState.value = UiState.Success }
                .onFailure { _uiState.value = UiState.Error("Ошибка добавления записи") }
        }
    }

    fun resetState() {
        _uiState.value = UiState.Idle
    }
}
