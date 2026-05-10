package com.vetuslugi.domain.usecase.place

import com.vetuslugi.domain.model.Place
import com.vetuslugi.domain.repository.PlaceRepository

class GetNurseryByAddressUseCase(private val repository: PlaceRepository) {
    suspend operator fun invoke(address: String): Result<Place> =
        repository.getNurseryByAddress(address)
}
