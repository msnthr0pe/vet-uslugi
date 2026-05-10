package com.vetuslugi.data.repository

import android.util.Base64
import com.vetuslugi.domain.model.Animal
import com.vetuslugi.domain.repository.AnimalRepository
import com.vetuslugi.ktor.AuthApi
import com.vetuslugi.ktor.AuthModels
import com.vetuslugi.ktor.ImgBBClient

class AnimalRepositoryImpl(private val api: AuthApi) : AnimalRepository {

    override suspend fun getAllAnimals(): Result<List<Animal>> = runCatching {
        api.getAnimals().map { it.toDomain() }
    }

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

    override suspend fun uploadImage(imageBytes: ByteArray, fileName: String): Result<String> = runCatching {
        val base64 = Base64.encodeToString(imageBytes, Base64.NO_WRAP)
        ImgBBClient.api.uploadImage(ImgBBClient.API_KEY, base64).data.url
    }

    private fun AuthModels.AnimalDTO.toDomain() = Animal(
        id = id, nickname = nickname, species = species, breed = breed,
        age = age, diseases = diseases, imageUrl = imageUrl,
        shelterAddress = shelterAddress, nurseryAddress = nurseryAddress
    )

    private fun Animal.toDto() = AuthModels.AnimalDTO(
        id = id, nickname = nickname, species = species, breed = breed,
        age = age, diseases = diseases, imageUrl = imageUrl,
        shelterAddress = shelterAddress, nurseryAddress = nurseryAddress
    )
}
