package com.vetuslugi.domain.usecase.news

import com.vetuslugi.domain.model.News
import com.vetuslugi.domain.repository.NewsRepository

class AddNewsUseCase(private val repository: NewsRepository) {
    suspend operator fun invoke(news: News): Result<Unit> =
        repository.addNews(news)
}
