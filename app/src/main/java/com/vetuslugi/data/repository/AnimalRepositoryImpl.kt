package com.vetuslugi.data.repository

import com.vetuslugi.domain.model.Animal
import com.vetuslugi.domain.repository.AnimalRepository
import com.vetuslugi.ktor.AuthApi
import com.vetuslugi.ktor.AuthModels

class AnimalRepositoryImpl(private val api: AuthApi) : AnimalRepository {

    override suspend fun getAnimalsByShelter(shelterAddress: String): Result<List<Animal>> = runCatching {
        api.getAnimalsByShelter(AuthModels.InfoDTO(shelterAddress)).map { it.toDomain() }
    }

    override suspend fun getAnimalsByNursery(nurseryAddress: String): Result<List<Animal>> = runCatching {
        api.getAnimalsByNursery(AuthModels.InfoDTO(nurseryAddress)).map { it.toDomain() }
    }

    override suspend fun addAnimal(animal: Animal): Result<Unit> = runCatching {
        api.addAnimal(animal.toDto())
        Unit
    }

    override suspend fun updateAnimal(animal: Animal): Result<Unit> = runCatching {
        api.updateAnimal(animal.toDto())
        Unit
    }

    override suspend fun deleteAnimal(id: String): Result<Unit> = runCatching {
        api.deleteAnimal(AuthModels.InfoDTO(id))
        Unit
    }

    private fun AuthModels.AnimalDTO.toDomain() = Animal(
        id = id, nickname = nickname, species = species, breed = breed,
        age = age, diseases = diseases,
        shelterAddress = shelterAddress, nurseryAddress = nurseryAddress
    )

    private fun Animal.toDto() = AuthModels.AnimalDTO(
        id = id, nickname = nickname, species = species, breed = breed,
        age = age, diseases = diseases,
        shelterAddress = shelterAddress, nurseryAddress = nurseryAddress
    )
}
