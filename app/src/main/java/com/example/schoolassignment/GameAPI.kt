package com.example.schoolassignment

import com.example.schoolassignment.game.*
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface GameAPI {
    @GET("/api/games")
    fun getAllGames(): Call<List<GameAllDTO>>

    @GET("api/games")
    fun getSpecificGame(@Query("id") id: Int): Call<GameDetailDTO>
}