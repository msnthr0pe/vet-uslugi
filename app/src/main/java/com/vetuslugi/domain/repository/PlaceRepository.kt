package com.vetuslugi.domain.repository

import com.vetuslugi.domain.model.Place

interface PlaceRepository {
    suspend fun getShelters(): Result<List<Place>>
    suspend fun getNurseries(): Result<List<Place>>
    suspend fun getSheltersByOwner(owner: String): Result<List<Place>>
    suspend fun getNurseriesByOwner(owner: String): Result<List<Place>>
    suspend fun addShelter(place: Place): Result<Unit>
    suspend fun addNursery(place: Place): Result<Unit>
    suspend fun updateShelter(place: Place): Result<Unit>
    suspend fun updateNursery(place: Place): Result<Unit>
}
