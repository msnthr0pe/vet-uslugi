package com.vetuslugi.data.repository

import com.vetuslugi.domain.model.News
import com.vetuslugi.domain.repository.NewsRepository
import com.vetuslugi.ktor.AuthApi
import com.vetuslugi.ktor.AuthModels

class NewsRepositoryImpl(private val api: AuthApi) : NewsRepository {

    override suspend fun getNews(): Result<List<News>> =
        runCatching { api.getNews().map { News(it.title, it.description) } }

    override suspend fun addNews(news: News): Result<Unit> =
        runCatching { api.addNews(AuthModels.NewsDTO(news.title, news.description)) }.map {}
}
