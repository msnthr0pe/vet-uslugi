package com.vetuslugi.domain.usecase.animal

import com.vetuslugi.domain.model.Animal
import com.vetuslugi.domain.repository.AnimalRepository
import kotlinx.coroutines.flow.Flow

class GetAnimalsByShelterStreamUseCase(private val repository: AnimalRepository) {
    operator fun invoke(shelterAddress: String): Flow<List<Animal>> =
        repository.getAnimalsByShelterStream(shelterAddress)
}
