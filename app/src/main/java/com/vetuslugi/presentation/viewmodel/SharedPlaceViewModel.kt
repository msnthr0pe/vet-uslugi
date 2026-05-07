package com.vetuslugi.presentation.viewmodel

import androidx.lifecycle.ViewModel
import com.vetuslugi.domain.model.Place
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class SharedPlaceViewModel : ViewModel() {

    enum class PlaceType(val label: String) {
        SHELTER("приюте"),
        NURSERY("питомнике")
    }

    data class PlaceSelection(
        val place: Place,
        val type: PlaceType,
        val editable: Boolean,
        val fromProfile: Boolean
    )

    private val _selection = MutableStateFlow<PlaceSelection?>(null)
    val selection: StateFlow<PlaceSelection?> = _selection

    fun select(place: Place, type: PlaceType, editable: Boolean, fromProfile: Boolean) {
        _selection.value = PlaceSelection(place, type, editable, fromProfile)
    }
}
