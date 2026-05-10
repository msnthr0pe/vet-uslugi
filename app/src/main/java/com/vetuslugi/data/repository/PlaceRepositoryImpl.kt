package com.vetuslugi.data.repository

import com.vetuslugi.domain.model.Place
import com.vetuslugi.domain.repository.PlaceRepository
import com.vetuslugi.ktor.AuthApi
import com.vetuslugi.ktor.AuthModels

class PlaceRepositoryImpl(private val api: AuthApi) : PlaceRepository {

    private fun Place.toDto() = AuthModels.PlaceDTO(address, name, phone, description, owner, clubAddress)
    private fun AuthModels.PlaceDTO.toDomain() = Place(address, name, phone, description, owner, clubAddress)

    override suspend fun getShelters(): Result<List<Place>> =
        runCatching { api.getShelters().map { it.toDomain() } }

    override suspend fun getNurseries(): Result<List<Place>> =
        runCatching { api.getNurseries().map { it.toDomain() } }

    override suspend fun getSheltersByOwner(owner: String): Result<List<Place>> =
        runCatching { api.getShelterBy(AuthModels.InfoDTO(owner)).map { it.toDomain() } }

    override suspend fun getNurseriesByOwner(owner: String): Result<List<Place>> =
        runCatching { api.getNurseryBy(AuthModels.InfoDTO(owner)).map { it.toDomain() } }

    override suspend fun getShelterByAddress(address: String): Result<Place> =
        runCatching { api.getShelterByAddress(AuthModels.InfoDTO(address)).toDomain() }

    override suspend fun getNurseryByAddress(address: String): Result<Place> =
        runCatching { api.getNurseryByAddress(AuthModels.InfoDTO(address)).toDomain() }

    override suspend fun addShelter(place: Place): Result<Unit> =
        runCatching { api.addShelter(place.toDto()) }.map {}

    override suspend fun addNursery(place: Place): Result<Unit> =
        runCatching { api.addNursery(place.toDto()) }.map {}

    override suspend fun updateShelter(place: Place): Result<Unit> =
        runCatching { api.updateShelter(place.toDto()) }.map {}

    override suspend fun updateNursery(place: Place): Result<Unit> =
        runCatching { api.updateNursery(place.toDto()) }.map {}
}
