package com.vetuslugi.domain.usecase.place

import com.vetuslugi.domain.model.Place
import com.vetuslugi.domain.repository.PlaceRepository

class GetSheltersByOwnerUseCase(private val repository: PlaceRepository) {
    suspend operator fun invoke(owner: String): Result<List<Place>> =
        repository.getSheltersByOwner(owner)
}
