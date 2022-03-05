package com.example.schoolassignment

import androidx.activity.ComponentActivity
import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
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
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import coil.compose.AsyncImage
import com.example.schoolassignment.game.GameAllDTO


const val GAME_OVERVIEW_ROUTE = "gameOverviewView"
const val GAME_FAVOURITES_ROUTE = "gameFavouritesView"

@Composable
fun GamePageView(mainNavController: NavHostController) {

    val gameNavController = rememberNavController()

    Scaffold(
        topBar = { TopAppBar(mainNavController) },
        bottomBar = { BottomAppBar(gameNavController) },

        ) {
        NavHost(navController = gameNavController, startDestination = GAME_OVERVIEW_ROUTE)
        {
            composable(route = GAME_OVERVIEW_ROUTE) {
                GameOverview(mainNavController)
            }
            composable(route = GAME_FAVOURITES_ROUTE) {
                GameFavourites()
            }
        }
    }

}

@Composable
fun GameOverview(mainNavController: NavHostController) {

    val userId =
        "EGL3WIE65YZfun6nfoCXpPlQBfD3"
    //Firebase.auth.currentUser!!.uid
    val gameVM = viewModel<GameViewModel>(LocalContext.current as ComponentActivity)
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
            items(count = gameVM.games.value.size, itemContent = { index: Int ->
                GameCard(
                    game = gameVM.games.value[index],
                    gameVM = gameVM,
                    isFavourite = gameVM.favouriteGameIds.value.any { it == gameVM.games.value[index].id },
                    mainNavController = mainNavController
                )
            })
        }
    }
}

@Composable
fun GameFavourites() {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Text(text = "GAME FAVOURITES")
    }
}

@Composable
fun TopAppBar(mainNavController: NavHostController) {

    val loginVM = viewModel<LoginViewModel>(LocalContext.current as ComponentActivity)
    val userEmail = loginVM.getUserEmail()


    TopAppBar(backgroundColor = Color(0xFF363333)) {

    }

//        Text(text = userEmail)
//        TextButton(onClick = {
//            loginVM.signOut()
//            mainNavController.navigate(LOGIN_PAGE_ROUTE)
//        }) {
//            Text(text = "SIGN OUT")
//        }
}

@Composable
fun BottomAppBar(gameNavController: NavHostController) {

    val navBackStackEntry by gameNavController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination

    BottomNavigation(
        backgroundColor = Color(0xFF363333),
    ) {
        BottomNavigationItem(
            icon = {
                Icon(
                    painter = painterResource(id = R.drawable.ic_baseline_videogame_asset_24),
                    contentDescription = "Game Overview"
                )
            },
            label = { Text(text = "Games") },
            selectedContentColor = Color(0xFFCCC4C4),
            unselectedContentColor = Color(0xFF817C7C),
            selected = currentDestination?.hierarchy?.any { it.route == GAME_OVERVIEW_ROUTE } == true,
            onClick = { gameNavController.navigate(GAME_OVERVIEW_ROUTE)},
        )

        BottomNavigationItem(
            icon = {
                Icon(
                    painter = painterResource(id = R.drawable.ic_baseline_star_24),
                    contentDescription = "Favourite games"
                )
            },
            label = { Text(text = "Favourites") },
            selectedContentColor = Color(0xFFCCC4C4),
            unselectedContentColor = Color(0xFF817C7C),
            selected = currentDestination?.hierarchy?.any { it.route == GAME_FAVOURITES_ROUTE } == true,
            onClick = { gameNavController.navigate(GAME_FAVOURITES_ROUTE) },
        )
    }
}

@Composable
fun GameCard(
    game: GameAllDTO,
    gameVM: GameViewModel,
    isFavourite: Boolean,
    mainNavController: NavHostController
) {
    Card(
        modifier = Modifier
            .height(125.dp)
            .padding(horizontal = 15.dp, vertical = 10.dp)
            .clickable {
                mainNavController.navigate("$SCAFFOLD_GAME_DETAIL_ROUTE/${game.id}")
            },
        shape = RoundedCornerShape(10.dp),
        elevation = 8.dp,
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            AsyncImage(
                model = game.thumbnail,
                modifier = Modifier
                    .fillMaxHeight()
                    .clip(RoundedCornerShape(topStart = 10.dp, bottomStart = 10.dp)),
                contentDescription = null
            )
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
}

@Composable
fun GameDetailView(mainNavController: NavHostController, gameId: String) {
    Text(text = gameId)


}