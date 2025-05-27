package com.vetuslugi.ktor

import com.vetuslugi.ktor.AuthModels.LoginDTO
import com.vetuslugi.ktor.AuthModels.NewsDTO
import com.vetuslugi.ktor.AuthModels.PlaceDTO
import com.vetuslugi.ktor.AuthModels.RegisterRequest
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

interface AuthApi {
    @POST("login")
    fun login(@Body request: AuthModels.LoginRequest): Call<AuthModels.AuthResponse>

    @POST("register")
    fun register(@Body request: AuthModels.RegisterRequest): Call<AuthModels.AuthResponse>

    @POST("addclient")
    fun addClient(@Body request: AuthModels.ClientRequest): Call<AuthModels.AuthResponse>

    @GET("getclients")
    suspend fun getClients(): List<AuthModels.ClientRequest>

    @POST("getclientby")
    suspend fun getClientBy(@Body request: AuthModels.ClientByDTO): List<AuthModels.ClientRequest>

    @POST("getuser")
    suspend fun getUser(@Body email: LoginDTO): RegisterRequest

    @GET("getnews")
    suspend fun getNews(): List<NewsDTO>

    @POST("addnews")
    fun addNews(@Body news: NewsDTO): Call<AuthModels.AuthResponse>

    @GET("getshelters")
    suspend fun getShelters(): List<PlaceDTO>

    @GET("getnurseries")
    suspend fun getNurseries(): List<PlaceDTO>

    @POST("updateshelter")
    suspend fun updateShelter(@Body request: PlaceDTO): AuthModels.InfoDTO

    @POST("updatenursery")
    suspend fun updateNursery(@Body request: PlaceDTO): AuthModels.InfoDTO

    @POST("getshelterby")
    suspend fun getShelterBy(@Body request: AuthModels.InfoDTO): List<PlaceDTO>

    @POST("getnurseryby")
    suspend fun getNurseryBy(@Body request: AuthModels.InfoDTO): List<PlaceDTO>

}