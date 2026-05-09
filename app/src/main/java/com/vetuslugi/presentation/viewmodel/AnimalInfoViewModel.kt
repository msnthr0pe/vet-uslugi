package com.vetuslugi.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.vetuslugi.domain.model.Animal
import com.vetuslugi.domain.usecase.animal.DeleteAnimalUseCase
import com.vetuslugi.domain.usecase.animal.UpdateAnimalUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class AnimalInfoViewModel(
    private val updateAnimalUseCase: UpdateAnimalUseCase,
    private val deleteAnimalUseCase: DeleteAnimalUseCase
) : ViewModel() {

    sealed class UiState {
        object Idle : UiState()
        object Loading : UiState()
        object Updated : UiState()
        object Deleted : UiState()
        data class Error(val message: String) : UiState()
    }

    private val _uiState = MutableStateFlow<UiState>(UiState.Idle)
    val uiState: StateFlow<UiState> = _uiState

    fun updateAnimal(animal: Animal) {
        viewModelScope.launch {
            _uiState.value = UiState.Loading
            updateAnimalUseCase(animal)
                .onSuccess { _uiState.value = UiState.Updated }
                .onFailure { _uiState.value = UiState.Error("Ошибка сохранения") }
        }
    }

    fun deleteAnimal(id: String) {
        viewModelScope.launch {
            _uiState.value = UiState.Loading
            deleteAnimalUseCase(id)
                .onSuccess { _uiState.value = UiState.Deleted }
                .onFailure { _uiState.value = UiState.Error("Ошибка удаления") }
        }
    }

    fun resetState() {
        _uiState.value = UiState.Idle
    }
}
