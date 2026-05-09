package com.vetuslugi.domain.usecase.place

import com.vetuslugi.domain.model.Place
import com.vetuslugi.domain.repository.ClubRepository

class UpdateClubUseCase(private val repository: ClubRepository) {
    suspend operator fun invoke(place: Place): Result<Unit> = repository.updateClub(place)
}
