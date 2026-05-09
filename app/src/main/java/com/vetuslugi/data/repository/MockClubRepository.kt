package com.vetuslugi.data.repository

import com.vetuslugi.domain.model.Place
import com.vetuslugi.domain.repository.ClubRepository

class MockClubRepository : ClubRepository {

    private val mockClubs = listOf(
        Place(
            address = "ул. Ленина, 12, Москва",
            name = "Клуб «Верный друг»",
            phone = "+7 (495) 123-45-67",
            description = "Объединяет несколько приютов и питомников Москвы. Помогаем животным найти дом.",
            owner = "-"
        ),
        Place(
            address = "пр. Мира, 34, Санкт-Петербург",
            name = "Клуб «Лапа помощи»",
            phone = "+7 (812) 987-65-43",
            description = "Городской клуб любителей животных. Координируем волонтёров и приюты.",
            owner = "-"
        ),
        Place(
            address = "ул. Садовая, 7, Казань",
            name = "Клуб «Зоозащита»",
            phone = "+7 (843) 456-78-90",
            description = "Региональное объединение по защите прав животных. Работаем с питомниками.",
            owner = "-"
        )
    )

    override suspend fun getClubs(): Result<List<Place>> =
        Result.success(mockClubs)

    override suspend fun getClubsByOwner(owner: String): Result<List<Place>> =
        Result.success(mockClubs.filter { it.owner == owner })

    override suspend fun addClub(place: Place): Result<Unit> =
        Result.success(Unit)

    override suspend fun updateClub(place: Place): Result<Unit> =
        Result.success(Unit)
}
