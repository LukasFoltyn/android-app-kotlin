package com.example.schoolassignment

import androidx.activity.ComponentActivity
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.example.schoolassignment.viewModels.GameOverviewViewModel

@Composable
fun GameFavourites(mainNavController: NavHostController) {
    val gameVM = viewModel<GameOverviewViewModel>(LocalContext.current as ComponentActivity)
    gameVM.showFavouritesPage = true


    val favouritesGames = gameVM.games.filter { game ->
        gameVM.favouriteGameIds.any { it == game.id }
    }

    if (favouritesGames.isEmpty()) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Text(text = "No favourite games!", fontSize = 25.sp)
        }
    } else {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            items(count = favouritesGames.size, itemContent = { index: Int ->
                GameCardBase(
                    mainNavController = mainNavController,
                    gameId = favouritesGames[index].id,
                    imgURL = favouritesGames[index].thumbnail
                ) {
                    Column(modifier = Modifier.padding(10.dp)) {
                        Text(
                            text = favouritesGames[index].title,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.fillMaxWidth()
                        )
                        Spacer(modifier = Modifier.height(3.dp))
                        Text(
                            text = favouritesGames[index].short_description,
                            overflow = TextOverflow.Ellipsis,
                            maxLines = 3
                        )
                    }
                }
            })
        }
    }
}