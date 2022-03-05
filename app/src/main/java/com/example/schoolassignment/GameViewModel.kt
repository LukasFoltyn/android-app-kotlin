package com.example.schoolassignment

import android.util.Log
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.schoolassignment.game.*
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.launch
import retrofit2.Retrofit
import retrofit2.awaitResponse
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.create

class GameViewModel : ViewModel() {

    val games: MutableState<List<GameAllDTO>> = mutableStateOf(listOf())
    val favouriteGameIds: MutableState<MutableList<Int>> = mutableStateOf(mutableListOf())
    val isLoadingGameIds = mutableStateOf(true)
    val isLoadingGames = mutableStateOf(true)
    val specificGame: MutableState<GameDetailDTO?> = mutableStateOf(null)

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
                games.value = result.body()!!
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
                    favouriteGameIds.value = it.get("gameIds") as MutableList<Int>
                }
                isLoadingGameIds.value = false
            }
    }

    fun getSpecificGame(id: Int) {
        viewModelScope.launch {
            val result = gameAPI.getSpecificGame(id).awaitResponse()
            if (result.isSuccessful) {
                specificGame.value = result.body()
            } else {
                //Error
            }
        }
    }

    fun addFavouriteGame(gameId: Int) {
        favouriteGameIds.value.add(gameId)
        updateFavouriteGames()
    }

    fun removeFavouriteGame(gameId: Int) {
        favouriteGameIds.value.remove(gameId)
        updateFavouriteGames()
    }

    private fun updateFavouriteGames() {
        Firebase.firestore
            .collection("favouriteGames")
            .document(Firebase.auth.currentUser!!.uid)
            .set(hashMapOf("gameIds" to favouriteGameIds.value))
    }

}