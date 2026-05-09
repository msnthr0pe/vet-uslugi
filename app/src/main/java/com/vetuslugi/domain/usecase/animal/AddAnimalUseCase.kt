package com.vetuslugi.domain.usecase.animal

import com.vetuslugi.domain.model.Animal
import com.vetuslugi.domain.repository.AnimalRepository

class AddAnimalUseCase(private val repository: AnimalRepository) {
    suspend operator fun invoke(animal: Animal): Result<Unit> =
        repository.addAnimal(animal)
}
