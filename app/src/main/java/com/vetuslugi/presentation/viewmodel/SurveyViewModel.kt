package com.vetuslugi.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.vetuslugi.domain.model.Animal
import com.vetuslugi.domain.model.SurveyResult
import com.vetuslugi.domain.usecase.animal.GetAllAnimalsUseCase
import com.vetuslugi.domain.usecase.survey.GetSurveyBreedsUseCase
import com.vetuslugi.domain.usecase.survey.GetSurveySpeciesUseCase
import com.vetuslugi.domain.usecase.survey.SubmitSurveyUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class SurveyViewModel(
    private val getSpeciesUseCase: GetSurveySpeciesUseCase,
    private val getBreedsUseCase: GetSurveyBreedsUseCase,
    private val submitSurveyUseCase: SubmitSurveyUseCase,
    private val getAllAnimalsUseCase: GetAllAnimalsUseCase
) : ViewModel() {

    sealed class UiState {
        object Idle : UiState()
        object Loading : UiState()
        object Submitted : UiState()
        data class Error(val message: String) : UiState()
    }

    private val _uiState = MutableStateFlow<UiState>(UiState.Idle)
    val uiState: StateFlow<UiState> = _uiState

    private val _species = MutableStateFlow<List<String>>(emptyList())
    val species: StateFlow<List<String>> = _species

    private val _breeds = MutableStateFlow<List<String>>(emptyList())
    val breeds: StateFlow<List<String>> = _breeds

    private val _results = MutableStateFlow<List<SurveyResult>>(emptyList())
    val results: StateFlow<List<SurveyResult>> = _results

    private val _allAnimals = MutableStateFlow<List<Animal>>(emptyList())
    val allAnimals: StateFlow<List<Animal>> = _allAnimals

    fun loadAllAnimals() {
        viewModelScope.launch {
            getAllAnimalsUseCase().onSuccess { _allAnimals.value = it }
        }
    }

    fun loadOptions() {
        viewModelScope.launch {
            getSpeciesUseCase().onSuccess { _species.value = it }
            getBreedsUseCase().onSuccess { _breeds.value = it }
        }
    }

    fun submitSurvey(
        species: String,
        breed: String,
        age: Int,
        willingToAdoptSick: Boolean,
        mostImportant: String
    ) {
        viewModelScope.launch {
            _uiState.value = UiState.Loading
            submitSurveyUseCase(species, breed, age, willingToAdoptSick, mostImportant)
                .onSuccess { results ->
                    _results.value = results
                    _uiState.value = UiState.Submitted
                }
                .onFailure { e ->
                    _uiState.value = UiState.Error("Ошибка: ${e.message}")
                }
        }
    }

    fun resetState() {
        _uiState.value = UiState.Idle
    }

    fun resetResults() {
        _results.value = emptyList()
    }
}
