package com.example.schoolassignment

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.schoolassignment.game.*
import kotlinx.coroutines.launch
import retrofit2.Retrofit
import retrofit2.awaitResponse
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.create

class GameViewModel: ViewModel() {

    val games: MutableState<List<GameAllDTO>> = mutableStateOf(listOf<GameAllDTO>())

    private val gameAPI: GameAPI by lazy {
        Retrofit
            .Builder()
            .baseUrl("https://www.freetogame.com")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create()
    }

    fun getAllGames() {
        viewModelScope.launch {
            val result = gameAPI.getAllGames().awaitResponse()
            if(result.isSuccessful) {
                games.value = result.body()!!
            } else {
                //error
            }
        }
    }

}