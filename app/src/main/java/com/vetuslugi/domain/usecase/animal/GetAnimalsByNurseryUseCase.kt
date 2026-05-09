package com.vetuslugi.domain.usecase.animal

import com.vetuslugi.domain.model.Animal
import com.vetuslugi.domain.repository.AnimalRepository

class GetAnimalsByNurseryUseCase(private val repository: AnimalRepository) {
    suspend operator fun invoke(address: String): Result<List<Animal>> =
        repository.getAnimalsByNursery(address)
}
