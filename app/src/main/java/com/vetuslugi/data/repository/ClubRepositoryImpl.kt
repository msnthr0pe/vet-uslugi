package com.vetuslugi.data.repository

import com.vetuslugi.domain.model.Place
import com.vetuslugi.domain.repository.ClubRepository
import com.vetuslugi.ktor.AuthApi
import com.vetuslugi.ktor.AuthModels

class ClubRepositoryImpl(private val api: AuthApi) : ClubRepository {

    private fun Place.toDto() = AuthModels.PlaceDTO(address, name, phone, description, owner, clubAddress)
    private fun AuthModels.PlaceDTO.toDomain() = Place(address, name, phone, description, owner, clubAddress)

    override suspend fun getClubs(): Result<List<Place>> =
        runCatching { api.getClubs().map { it.toDomain() } }

    override suspend fun getClubsByOwner(owner: String): Result<List<Place>> =
        runCatching { api.getClubBy(AuthModels.InfoDTO(owner)).map { it.toDomain() } }

    override suspend fun addClub(place: Place): Result<Unit> =
        runCatching { api.addClub(place.toDto()) }.map {}

    override suspend fun updateClub(place: Place): Result<Unit> =
        runCatching { api.updateClub(place.toDto()) }.map {}
}
