package com.vetuslugi.domain.repository

import com.vetuslugi.domain.model.Animal

interface AnimalRepository {
    suspend fun getAllAnimals(): Result<List<Animal>>
    suspend fun getAnimalsByShelter(shelterAddress: String): Result<List<Animal>>
    suspend fun getAnimalsByNursery(nurseryAddress: String): Result<List<Animal>>
    suspend fun addAnimal(animal: Animal): Result<Unit>
    suspend fun updateAnimal(animal: Animal): Result<Unit>
    suspend fun deleteAnimal(id: String): Result<Unit>
    suspend fun uploadImage(imageBytes: ByteArray, fileName: String): Result<String>
}
