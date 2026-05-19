package com.vetuslugi.domain.repository

import com.vetuslugi.domain.model.SurveyResult

interface SurveyRepository {
    suspend fun getSpecies(): Result<List<String>>
    suspend fun getBreeds(): Result<List<String>>
    suspend fun submitSurvey(
        species: String,
        breed: String,
        age: Int,
        willingToAdoptSick: Boolean,
        mostImportant: String
    ): Result<List<SurveyResult>>
}
