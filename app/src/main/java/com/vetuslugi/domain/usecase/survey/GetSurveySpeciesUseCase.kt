package com.vetuslugi.domain.usecase.survey

import com.vetuslugi.domain.repository.SurveyRepository

class GetSurveySpeciesUseCase(private val repository: SurveyRepository) {
    suspend operator fun invoke(): Result<List<String>> = repository.getSpecies()
}
