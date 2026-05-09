package com.vetuslugi.domain.usecase.animal

import com.vetuslugi.domain.repository.AnimalRepository

class DeleteAnimalUseCase(private val repository: AnimalRepository) {
    suspend operator fun invoke(id: String): Result<Unit> =
        repository.deleteAnimal(id)
}
