package com.vetuslugi.domain.usecase.place

import com.vetuslugi.domain.model.Place
import com.vetuslugi.domain.repository.ClubRepository

class GetClubsByOwnerUseCase(private val repository: ClubRepository) {
    suspend operator fun invoke(owner: String): Result<List<Place>> =
        repository.getClubsByOwner(owner)
}
