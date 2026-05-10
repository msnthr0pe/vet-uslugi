package com.vetuslugi.ktor

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object ImgBBClient {
    private val retrofit = Retrofit.Builder()
        .baseUrl("https://api.imgbb.com/1/")
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    val api: ImgBBApi = retrofit.create(ImgBBApi::class.java)

    const val API_KEY = "b879ffbef68b5cdc5936cd7031a70d1f"
}
