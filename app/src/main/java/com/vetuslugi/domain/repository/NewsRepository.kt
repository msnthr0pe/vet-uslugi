package com.vetuslugi.domain.repository

import com.vetuslugi.domain.model.News

interface NewsRepository {
    suspend fun getNews(): Result<List<News>>
    suspend fun addNews(news: News): Result<Unit>
}
