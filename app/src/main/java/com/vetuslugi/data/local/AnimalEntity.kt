package com.vetuslugi.data.local

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.vetuslugi.domain.model.Animal

@Entity(tableName = "animals")
data class AnimalEntity(
    @PrimaryKey val id: String,
    val nickname: String,
    val species: String,
    val breed: String,
    val age: Int,
    val diseases: String?,
    @ColumnInfo(defaultValue = "0") val diseaseSeverity: Int = 0,
    val imageUrl: String?,
    val shelterAddress: String?,
    val nurseryAddress: String?
) {
    fun toDomain() = Animal(
        id = id,
        nickname = nickname,
        species = species,
        breed = breed,
        age = age,
        diseases = diseases,
        diseaseSeverity = diseaseSeverity,
        imageUrl = imageUrl,
        shelterAddress = shelterAddress,
        nurseryAddress = nurseryAddress
    )
}

fun Animal.toEntity() = AnimalEntity(
    id = id,
    nickname = nickname,
    species = species,
    breed = breed,
    age = age,
    diseases = diseases,
    diseaseSeverity = diseaseSeverity,
    imageUrl = imageUrl,
    shelterAddress = shelterAddress,
    nurseryAddress = nurseryAddress
)
