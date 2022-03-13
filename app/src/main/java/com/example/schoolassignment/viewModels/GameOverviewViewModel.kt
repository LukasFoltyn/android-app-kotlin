package com.example.schoolassignment.viewModels

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.schoolassignment.GameAPI
import com.example.schoolassignment.gameDTOs.GameAllDTO
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.launch
import retrofit2.Retrofit
import retrofit2.awaitResponse
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.create



class GameOverviewViewModel : ViewModel() {

    var games: List<GameAllDTO> = listOf()
    var favouriteGameIds: MutableList<Int> = mutableListOf()
    var showFavouritesPage: Boolean = false


    val isLoadingGameIds = mutableStateOf(true)
    val isLoadingGames = mutableStateOf(true)

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
            if (result.isSuccessful) {
                games = result.body()!!
            } else {
                //Error
            }

            isLoadingGames.value = false
        }
    }

    fun getUsersFavouriteGames(userId: String) {

        Firebase.firestore
            .collection("favouriteGames")
            .document(userId)
            .get()
            .addOnSuccessListener {
                if (it.data != null) {
                    favouriteGameIds = it.get("gameIds") as MutableList<Int>
                }
                isLoadingGameIds.value = false
            }
    }

    fun addFavouriteGame(gameId: Int) {
        favouriteGameIds.add(gameId)
        updateFavouriteGames()
    }

    fun removeFavouriteGame(gameId: Int) {
        var removeIdx = 0;
        favouriteGameIds.forEachIndexed { index, id ->
            if (id == gameId) {
                removeIdx = index
            }
        }
        favouriteGameIds.removeAt(removeIdx)
        updateFavouriteGames()
    }

    private fun updateFavouriteGames() {
        Firebase.firestore
            .collection("favouriteGames")
            .document(Firebase.auth.currentUser!!.uid)
            .set(hashMapOf("gameIds" to favouriteGameIds))
    }

}