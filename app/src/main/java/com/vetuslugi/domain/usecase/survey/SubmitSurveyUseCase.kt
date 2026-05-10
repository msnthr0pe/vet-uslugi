package com.vetuslugi.domain.usecase.survey

import com.vetuslugi.domain.model.SurveyResult
import com.vetuslugi.domain.repository.SurveyRepository

class SubmitSurveyUseCase(private val repository: SurveyRepository) {
    suspend operator fun invoke(
        species: String,
        breed: String,
        willingToAdoptSick: Boolean,
        mostImportant: String
    ): Result<List<SurveyResult>> =
        repository.submitSurvey(species, breed, willingToAdoptSick, mostImportant)
}
