package com.example.schoolassignment.viewModels

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.schoolassignment.GameAPI
import com.example.schoolassignment.Note
import com.example.schoolassignment.gameDTOs.GameDetailDTO
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.Timestamp
import kotlinx.coroutines.launch
import retrofit2.Retrofit
import retrofit2.awaitResponse
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.create

class GameDetailViewModel : ViewModel() {

    val specificGame: MutableState<GameDetailDTO?> = mutableStateOf(null)
    val specificGameNotes: MutableState<List<Note>> = mutableStateOf(listOf())
    val isLoadingNotes = mutableStateOf(true)


    private val gameAPI: GameAPI by lazy {
        Retrofit
            .Builder()
            .baseUrl("https://www.freetogame.com")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create()
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

    fun getSpecificGameNotes() {
        Firebase.firestore
            .collection("favouriteGames/${Firebase.auth.currentUser!!.uid}/notes")
            .whereEqualTo("gameId", specificGame.value!!.id)
            .orderBy("creationDate")
            .get()
            .addOnSuccessListener {
                val notes = mutableListOf<Note>()

                it.forEach { doc ->
                    notes.add(Note(doc.id, doc["note"] as String))
                }
                specificGameNotes.value = notes
                isLoadingNotes.value = false
            }
    }

    fun removeNote(noteId: String) {
        Firebase.firestore
            .collection("favouriteGames/${Firebase.auth.currentUser!!.uid}/notes")
            .document(noteId)
            .delete().addOnSuccessListener {
                getSpecificGameNotes()
            }
    }

    fun addNote(note: String) {
        Firebase.firestore
            .collection("favouriteGames/${Firebase.auth.currentUser!!.uid}/notes")
            .add(
                hashMapOf(
                    "gameId" to specificGame.value!!.id,
                    "note" to note,
                    "creationDate" to Timestamp.now()
                )
            ).addOnSuccessListener {
                getSpecificGameNotes()
            }
    }
}