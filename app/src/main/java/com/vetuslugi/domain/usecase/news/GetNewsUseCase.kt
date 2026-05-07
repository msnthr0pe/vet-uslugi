package com.vetuslugi.domain.usecase.news

import com.vetuslugi.domain.model.News
import com.vetuslugi.domain.repository.NewsRepository

class GetNewsUseCase(private val repository: NewsRepository) {
    suspend operator fun invoke(): Result<List<News>> =
        repository.getNews()
}
