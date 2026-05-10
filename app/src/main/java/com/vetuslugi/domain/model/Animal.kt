package com.vetuslugi.domain.model

data class Animal(
    val id: String = "",
    val nickname: String,
    val species: String,
    val breed: String,
    val age: Int,
    val diseases: String? = null,
    val imageUrl: String? = null,
    val shelterAddress: String? = null,
    val nurseryAddress: String? = null
)
