package com.vetuslugi.domain.usecase.place

import com.vetuslugi.domain.model.Place
import com.vetuslugi.domain.repository.PlaceRepository

class UpdateNurseryUseCase(private val repository: PlaceRepository) {
    suspend operator fun invoke(place: Place): Result<Unit> =
        repository.updateNursery(place)
}
