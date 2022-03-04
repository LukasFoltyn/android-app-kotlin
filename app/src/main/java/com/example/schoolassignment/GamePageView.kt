package com.example.schoolassignment

import android.util.Log
import androidx.activity.ComponentActivity
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.Composable
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
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import coil.compose.AsyncImage
import java.time.format.TextStyle

const val GAME_OVERVIEW_ROUTE = "gameOverviewView"
const val GAME_FAVOURITES_ROUTE = "gameFavouritesView"

@Composable
fun GamePageView(mainNavController: NavHostController) {

    val gameNavController = rememberNavController()

    Scaffold(
        topBar = { TopAppBar(mainNavController) },
        bottomBar = { BottomAppBar() },

        ) {
        NavHost(navController = gameNavController, startDestination = GAME_OVERVIEW_ROUTE)
        {
            composable(route = GAME_OVERVIEW_ROUTE) {
                GameOverview()
            }
            composable(route = GAME_FAVOURITES_ROUTE) {
                GameFavourites()
            }
        }
    }

}

@Composable
fun GameOverview() {

    val gameVM = viewModel<GameViewModel>(LocalContext.current as ComponentActivity)
    gameVM.getAllGames()

    if (gameVM.games.value.isEmpty()) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
    } else {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            items(count = gameVM.games.value.size, itemContent = { index: Int ->
                Card(
                    modifier = Modifier
                        .height(125.dp)
                        .padding(horizontal = 15.dp, vertical = 10.dp)
                        .clickable { Log.d("********", gameVM.games.value[index].id.toString()) },
                    shape = RoundedCornerShape(10.dp),
                    elevation = 8.dp,
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        AsyncImage(
                            model = gameVM.games.value[index].thumbnail,
                            modifier = Modifier
                                .fillMaxHeight()
                                .clip(RoundedCornerShape(topStart = 10.dp, bottomStart = 10.dp)),
                            contentDescription = null
                        )
                        Column(modifier = Modifier.padding(10.dp)) {
                            Row {
                                Text(
                                    modifier = Modifier.weight(6f),
                                    text = gameVM.games.value[index].title,
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Icon(
                                    modifier = Modifier.weight(1f),
                                    painter = painterResource(id = R.drawable.ic_baseline_favorite_24),
                                    contentDescription = "Add to favourites",
                                )
                            }

                            Spacer(modifier = Modifier.height(3.dp))
                            Text(
                                text = gameVM.games.value[index].short_description,
                                overflow = TextOverflow.Ellipsis,
                                maxLines = 3
                            )
                        }

                    }
                }
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

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.Yellow)
            .padding(15.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    )
    {
        Icon(
            painter = painterResource(id = R.drawable.ic_baseline_visibility_24),
            contentDescription = "Filter drawer"
        )
        if (userEmail != null) {
            Text(text = userEmail)
        }
        TextButton(onClick = {
            loginVM.signOut()
            mainNavController.navigate(LOGIN_PAGE_ROUTE)
        }) {
            Text(text = "SIGN OUT")
        }
    }
}

@Composable
fun BottomAppBar() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.Yellow)
            .padding(15.dp)
    ) {
        IconButton(onClick = { /*TODO*/ }) {

        }
    }
}