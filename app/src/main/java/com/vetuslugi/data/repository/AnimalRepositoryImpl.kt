package com.vetuslugi.data.repository

import android.util.Base64
import com.vetuslugi.data.local.AnimalDao
import com.vetuslugi.data.local.toEntity
import com.vetuslugi.domain.model.Animal
import com.vetuslugi.domain.repository.AnimalRepository
import com.vetuslugi.ktor.AuthApi
import com.vetuslugi.ktor.AuthModels
import com.vetuslugi.ktor.ImgBBClient
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class AnimalRepositoryImpl(
    private val api: AuthApi,
    private val animalDao: AnimalDao
) : AnimalRepository {

    override fun getAnimalsByShelterStream(shelterAddress: String): Flow<List<Animal>> =
        animalDao.getAnimalsByShelterStream(shelterAddress).map { list -> list.map { it.toDomain() } }

    override fun getAnimalsByNurseryStream(nurseryAddress: String): Flow<List<Animal>> =
        animalDao.getAnimalsByNurseryStream(nurseryAddress).map { list -> list.map { it.toDomain() } }

    override suspend fun getAllAnimals(): Result<List<Animal>> = runCatching {
        api.getAnimals().map { it.toDomain() }
    }

    override suspend fun getAnimalsByShelter(shelterAddress: String): Result<List<Animal>> = runCatching {
        val animals = api.getAnimalsByShelter(AuthModels.InfoDTO(shelterAddress)).map { it.toDomain() }
        animalDao.replaceByShelterAddress(shelterAddress, animals.map { it.toEntity() })
        animals
    }

    override suspend fun getAnimalsByNursery(nurseryAddress: String): Result<List<Animal>> = runCatching {
        val animals = api.getAnimalsByNursery(AuthModels.InfoDTO(nurseryAddress)).map { it.toDomain() }
        animalDao.replaceByNurseryAddress(nurseryAddress, animals.map { it.toEntity() })
        animals
    }

    override suspend fun addAnimal(animal: Animal): Result<Unit> = runCatching {
        api.addAnimal(animal.toDto())
        val address = animal.shelterAddress ?: animal.nurseryAddress
        if (address != null) {
            if (animal.nurseryAddress != null) {
                val updated = api.getAnimalsByNursery(AuthModels.InfoDTO(address)).map { it.toDomain() }
                animalDao.replaceByNurseryAddress(address, updated.map { it.toEntity() })
            } else {
                val updated = api.getAnimalsByShelter(AuthModels.InfoDTO(address)).map { it.toDomain() }
                animalDao.replaceByShelterAddress(address, updated.map { it.toEntity() })
            }
        }
        Unit
    }

    override suspend fun updateAnimal(animal: Animal): Result<Unit> = runCatching {
        api.updateAnimal(animal.toDto())
        animalDao.insertAll(listOf(animal.toEntity()))
        Unit
    }

    override suspend fun deleteAnimal(id: String): Result<Unit> = runCatching {
        api.deleteAnimal(AuthModels.InfoDTO(id))
        animalDao.deleteById(id)
        Unit
    }

    override suspend fun uploadImage(imageBytes: ByteArray, fileName: String): Result<String> = runCatching {
        val base64 = Base64.encodeToString(imageBytes, Base64.NO_WRAP)
        ImgBBClient.api.uploadImage(ImgBBClient.API_KEY, base64).data.url
    }

    private fun AuthModels.AnimalDTO.toDomain() = Animal(
        id = id, nickname = nickname, species = species, breed = breed,
        age = age, diseases = diseases, diseaseSeverity = diseaseSeverity,
        imageUrl = imageUrl, shelterAddress = shelterAddress, nurseryAddress = nurseryAddress
    )

    private fun Animal.toDto() = AuthModels.AnimalDTO(
        id = id, nickname = nickname, species = species, breed = breed,
        age = age, diseases = diseases, diseaseSeverity = diseaseSeverity,
        imageUrl = imageUrl, shelterAddress = shelterAddress, nurseryAddress = nurseryAddress
    )
}
