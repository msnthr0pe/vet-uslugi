package com.vetuslugi.domain.usecase.place

import com.vetuslugi.domain.model.Place
import com.vetuslugi.domain.repository.PlaceRepository

class GetNurseriesUseCase(private val repository: PlaceRepository) {
    suspend operator fun invoke(): Result<List<Place>> =
        repository.getNurseries()
}
