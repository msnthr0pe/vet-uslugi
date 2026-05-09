package com.vetuslugi.domain.usecase.place

import com.vetuslugi.domain.model.Place
import com.vetuslugi.domain.repository.ClubRepository

class GetClubsUseCase(private val repository: ClubRepository) {
    suspend operator fun invoke(): Result<List<Place>> = repository.getClubs()
}
