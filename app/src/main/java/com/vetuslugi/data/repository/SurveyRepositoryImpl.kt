package com.vetuslugi.data.repository

import com.vetuslugi.domain.model.Animal
import com.vetuslugi.domain.model.SurveyResult
import com.vetuslugi.domain.repository.SurveyRepository
import com.vetuslugi.ktor.AuthApi
import com.vetuslugi.ktor.AuthModels

class SurveyRepositoryImpl(private val api: AuthApi) : SurveyRepository {

    override suspend fun getSpecies(): Result<List<String>> = runCatching {
        api.getSurveySpecies()
    }

    override suspend fun getBreeds(): Result<List<String>> = runCatching {
        api.getSurveyBreeds()
    }

    override suspend fun submitSurvey(
        species: String,
        breed: String,
        willingToAdoptSick: Boolean,
        mostImportant: String
    ): Result<List<SurveyResult>> = runCatching {
        api.submitSurvey(
            AuthModels.SurveyRequest(species, breed, willingToAdoptSick, mostImportant)
        ).map { dto ->
            SurveyResult(
                animal = Animal(
                    id = dto.animal.id,
                    nickname = dto.animal.nickname,
                    species = dto.animal.species,
                    breed = dto.animal.breed,
                    age = dto.animal.age,
                    diseases = dto.animal.diseases,
                    shelterAddress = dto.animal.shelterAddress,
                    nurseryAddress = dto.animal.nurseryAddress
                ),
                coefficient = dto.coefficient
            )
        }
    }
}
