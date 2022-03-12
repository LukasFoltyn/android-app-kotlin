package com.example.schoolassignment

import com.example.schoolassignment.gameDTOs.*
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface GameAPI {
    @GET("/api/games")
    fun getAllGames(): Call<List<GameAllDTO>>

    @GET("/api/game")
    fun getSpecificGame(@Query("id") id: Int): Call<GameDetailDTO>
}