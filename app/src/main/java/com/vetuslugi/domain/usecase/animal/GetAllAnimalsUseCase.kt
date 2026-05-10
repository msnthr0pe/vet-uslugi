package com.vetuslugi.domain.usecase.animal

import com.vetuslugi.domain.model.Animal
import com.vetuslugi.domain.repository.AnimalRepository

class GetAllAnimalsUseCase(private val repository: AnimalRepository) {
    suspend operator fun invoke(): Result<List<Animal>> = repository.getAllAnimals()
}
