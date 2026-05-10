package com.vetuslugi.domain.usecase.place

import com.vetuslugi.domain.model.Place
import com.vetuslugi.domain.repository.PlaceRepository

class GetShelterByAddressUseCase(private val repository: PlaceRepository) {
    suspend operator fun invoke(address: String): Result<Place> =
        repository.getShelterByAddress(address)
}
