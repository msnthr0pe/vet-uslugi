package com.vetuslugi.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.vetuslugi.domain.model.Animal
import com.vetuslugi.domain.usecase.animal.AddAnimalUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class AddAnimalViewModel(
    private val addAnimalUseCase: AddAnimalUseCase
) : ViewModel() {

    sealed class UiState {
        object Idle : UiState()
        object Loading : UiState()
        object Success : UiState()
        data class Error(val message: String) : UiState()
    }

    private val _uiState = MutableStateFlow<UiState>(UiState.Idle)
    val uiState: StateFlow<UiState> = _uiState

    fun addAnimal(
        nickname: String,
        species: String,
        breed: String,
        age: Int,
        diseases: String?,
        shelterAddress: String?,
        nurseryAddress: String?
    ) {
        val animal = Animal(
            nickname = nickname, species = species, breed = breed, age = age, diseases = diseases,
            shelterAddress = shelterAddress, nurseryAddress = nurseryAddress
        )
        viewModelScope.launch {
            _uiState.value = UiState.Loading
            addAnimalUseCase(animal)
                .onSuccess { _uiState.value = UiState.Success }
                .onFailure { _uiState.value = UiState.Error("Ошибка добавления животного") }
        }
    }

    fun resetState() {
        _uiState.value = UiState.Idle
    }
}
