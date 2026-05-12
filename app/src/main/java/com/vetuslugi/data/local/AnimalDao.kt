package com.vetuslugi.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import kotlinx.coroutines.flow.Flow

@Dao
interface AnimalDao {

    @Query("SELECT * FROM animals WHERE shelterAddress = :address")
    fun getAnimalsByShelterStream(address: String): Flow<List<AnimalEntity>>

    @Query("SELECT * FROM animals WHERE nurseryAddress = :address")
    fun getAnimalsByNurseryStream(address: String): Flow<List<AnimalEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(animals: List<AnimalEntity>)

    @Query("DELETE FROM animals WHERE shelterAddress = :address")
    suspend fun deleteByShelterAddress(address: String)

    @Query("DELETE FROM animals WHERE nurseryAddress = :address")
    suspend fun deleteByNurseryAddress(address: String)

    @Query("DELETE FROM animals WHERE id = :id")
    suspend fun deleteById(id: String)

    @Transaction
    suspend fun replaceByShelterAddress(address: String, animals: List<AnimalEntity>) {
        deleteByShelterAddress(address)
        insertAll(animals)
    }

    @Transaction
    suspend fun replaceByNurseryAddress(address: String, animals: List<AnimalEntity>) {
        deleteByNurseryAddress(address)
        insertAll(animals)
    }
}
