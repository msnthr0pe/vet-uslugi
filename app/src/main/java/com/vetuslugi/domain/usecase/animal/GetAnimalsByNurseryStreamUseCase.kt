package com.vetuslugi.domain.usecase.animal

import com.vetuslugi.domain.model.Animal
import com.vetuslugi.domain.repository.AnimalRepository
import kotlinx.coroutines.flow.Flow

class GetAnimalsByNurseryStreamUseCase(private val repository: AnimalRepository) {
    operator fun invoke(nurseryAddress: String): Flow<List<Animal>> =
        repository.getAnimalsByNurseryStream(nurseryAddress)
}
