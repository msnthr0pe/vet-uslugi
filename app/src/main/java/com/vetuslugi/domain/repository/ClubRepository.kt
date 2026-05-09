package com.vetuslugi.domain.repository

import com.vetuslugi.domain.model.Place

interface ClubRepository {
    suspend fun getClubs(): Result<List<Place>>
    suspend fun getClubsByOwner(owner: String): Result<List<Place>>
    suspend fun addClub(place: Place): Result<Unit>
    suspend fun updateClub(place: Place): Result<Unit>
}
