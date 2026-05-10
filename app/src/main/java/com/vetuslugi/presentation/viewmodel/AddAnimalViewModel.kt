package com.vetuslugi.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.vetuslugi.domain.model.Animal
import com.vetuslugi.domain.usecase.animal.AddAnimalUseCase
import com.vetuslugi.domain.usecase.animal.UploadAnimalImageUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class AddAnimalViewModel(
    private val addAnimalUseCase: AddAnimalUseCase,
    private val uploadImageUseCase: UploadAnimalImageUseCase
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
        nurseryAddress: String?,
        imageBytes: ByteArray? = null,
        imageFileName: String = "image.jpg"
    ) {
        viewModelScope.launch {
            _uiState.value = UiState.Loading
            var imageUrl: String? = null
            if (imageBytes != null) {
                val uploadResult = uploadImageUseCase(imageBytes, imageFileName)
                uploadResult.onSuccess { imageUrl = it }
                uploadResult.onFailure {
                    _uiState.value = UiState.Error("Ошибка загрузки фото")
                    return@launch
                }
            }
            val animal = Animal(
                nickname = nickname, species = species, breed = breed, age = age,
                diseases = diseases, imageUrl = imageUrl,
                shelterAddress = shelterAddress, nurseryAddress = nurseryAddress
            )
            addAnimalUseCase(animal)
                .onSuccess { _uiState.value = UiState.Success }
                .onFailure { _uiState.value = UiState.Error("Ошибка добавления животного") }
        }
    }

    fun resetState() {
        _uiState.value = UiState.Idle
    }
}
