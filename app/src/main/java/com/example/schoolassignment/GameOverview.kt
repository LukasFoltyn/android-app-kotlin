package com.example.schoolassignment

import androidx.activity.ComponentActivity
import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import com.example.schoolassignment.gameDTOs.GameAllDTO
import com.example.schoolassignment.viewModels.GameOverviewViewModel
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase


@Composable
fun GameOverview(mainNavController: NavHostController) {
    val userId = Firebase.auth.currentUser!!.uid
    val gameVM = viewModel<GameOverviewViewModel>(LocalContext.current as ComponentActivity)
    gameVM.showFavouritesPage = false
    gameVM.getAllGames()
    gameVM.getUsersFavouriteGames(userId)

    if (gameVM.isLoadingGames.value || gameVM.isLoadingGameIds.value) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
    } else {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            items(count = gameVM.games.size, itemContent = { index: Int ->
                GameOverviewCard(
                    game = gameVM.games[index],
                    gameVM = gameVM,
                    isFavourite = gameVM.favouriteGameIds.any { it == gameVM.games[index].id },
                    mainNavController = mainNavController
                )
            })
        }
    }
}

@Composable
fun GameOverviewCard(
    game: GameAllDTO,
    gameVM: GameOverviewViewModel,
    isFavourite: Boolean,
    mainNavController: NavHostController
) {
    GameCardBase(mainNavController = mainNavController, gameId = game.id, imgURL = game.thumbnail) {
        Column(modifier = Modifier.padding(10.dp)) {
            Row {

                var isFavouriteChecked by remember { mutableStateOf(isFavourite) }
                val iconTint by animateColorAsState(
                    if (isFavouriteChecked) Color(0xFFEC407A) else Color(0xFFB0BEC5)
                )

                Text(
                    modifier = Modifier.weight(6f),
                    text = game.title,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
                Icon(
                    modifier = Modifier
                        .weight(1f)
                        .clickable {
                            isFavouriteChecked = !isFavouriteChecked
                            if (isFavouriteChecked) {
                                gameVM.addFavouriteGame(game.id)
                            } else {
                                gameVM.removeFavouriteGame(game.id)
                            }
                        },
                    painter = painterResource(id = R.drawable.ic_baseline_favorite_24),
                    contentDescription = "Add to favourites",
                    tint = iconTint
                )
            }
            Spacer(modifier = Modifier.height(3.dp))
            Text(
                text = game.short_description,
                overflow = TextOverflow.Ellipsis,
                maxLines = 3
            )
        }
    }
}

@Composable
fun GameCardBase(
    mainNavController: NavHostController,
    gameId: Int,
    imgURL: String,
    content: @Composable () -> Unit
) {
    Card(
        modifier = Modifier
            .height(125.dp)
            .padding(horizontal = 15.dp, vertical = 10.dp)
            .clickable {
                mainNavController.navigate("$SCAFFOLD_GAME_DETAIL_ROUTE/${gameId}")
            },
        shape = RoundedCornerShape(10.dp),
        elevation = 8.dp,
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            AsyncImage(
                model = imgURL,
                modifier = Modifier
                    .fillMaxHeight()
                    .clip(RoundedCornerShape(topStart = 10.dp, bottomStart = 10.dp)),
                contentDescription = null
            )
            content()
        }
    }
}