package com.vetuslugi.domain.model

data class User(
    val login: String,
    val password: String,
    val name: String,
    val surname: String,
    val phone: String,
    val role: String
)
