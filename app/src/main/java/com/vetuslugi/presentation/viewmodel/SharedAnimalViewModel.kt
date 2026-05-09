package com.vetuslugi.presentation.viewmodel

import androidx.lifecycle.ViewModel
import com.vetuslugi.domain.model.Animal
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class SharedAnimalViewModel : ViewModel() {

    data class AnimalContext(
        val placeAddress: String,
        val isNursery: Boolean,
        val editable: Boolean
    )

    data class AnimalSelection(
        val animal: Animal,
        val editable: Boolean
    )

    private val _context = MutableStateFlow<AnimalContext?>(null)
    val context: StateFlow<AnimalContext?> = _context

    private val _selection = MutableStateFlow<AnimalSelection?>(null)
    val selection: StateFlow<AnimalSelection?> = _selection

    fun setContext(placeAddress: String, isNursery: Boolean, editable: Boolean) {
        _context.value = AnimalContext(placeAddress, isNursery, editable)
    }

    fun selectAnimal(animal: Animal, editable: Boolean) {
        _selection.value = AnimalSelection(animal, editable)
    }
}
