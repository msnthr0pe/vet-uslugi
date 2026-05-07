package com.vetuslugi.domain.usecase.place

import com.vetuslugi.domain.model.Place
import com.vetuslugi.domain.repository.PlaceRepository

class AddShelterUseCase(private val repository: PlaceRepository) {
    suspend operator fun invoke(place: Place): Result<Unit> =
        repository.addShelter(place)
}
