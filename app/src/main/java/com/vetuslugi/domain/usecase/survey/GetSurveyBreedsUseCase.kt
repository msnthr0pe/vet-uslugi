package com.vetuslugi.domain.usecase.survey

import com.vetuslugi.domain.repository.SurveyRepository

class GetSurveyBreedsUseCase(private val repository: SurveyRepository) {
    suspend operator fun invoke(): Result<List<String>> = repository.getBreeds()
}
