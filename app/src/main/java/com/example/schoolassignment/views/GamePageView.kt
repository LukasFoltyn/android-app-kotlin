package com.example.schoolassignment.views

import androidx.activity.ComponentActivity
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.schoolassignment.GameFavourites
import com.example.schoolassignment.GameOverview
import com.example.schoolassignment.LOGIN_PAGE_ROUTE
import com.example.schoolassignment.R
import com.example.schoolassignment.viewModels.GameOverviewViewModel
import com.example.schoolassignment.viewModels.LoginViewModel

sealed class BottomNavItem(val title: String, val icon: Int, val screen_route: String) {
    object GameOverview :
        BottomNavItem("Games", R.drawable.ic_baseline_videogame_asset_24, "gameOverviewView")

    object GameFavourites :
        BottomNavItem("Favourites", R.drawable.ic_baseline_star_24, "gameFavouritesView")
}

@Composable
fun GamePageView(mainNavController: NavHostController) {

    val gameNavController = rememberNavController()

    Scaffold(
        topBar = { GamesTopAppBar(mainNavController) },
        bottomBar = { BottomAppBar(gameNavController) },

        ) {
        NavHost(
            navController = gameNavController,
            startDestination = BottomNavItem.GameOverview.screen_route
        )
        {
            composable(route = BottomNavItem.GameOverview.screen_route) {
                GameOverview(mainNavController)
            }
            composable(route = BottomNavItem.GameFavourites.screen_route) {
                GameFavourites(mainNavController)
            }
        }
    }

}

@Composable
fun BottomAppBar(gameNavController: NavHostController) {

    val navBackStackEntry by gameNavController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination

    val navItems = listOf(
        BottomNavItem.GameOverview,
        BottomNavItem.GameFavourites
    )

    BottomNavigation(
        backgroundColor = Color(0xFF363333),
    ) {

        navItems.forEach { navItem ->
            BottomNavigationItem(
                icon = {
                    Icon(
                        painter = painterResource(id = navItem.icon),
                        contentDescription = null
                    )
                },
                label = { Text(text = navItem.title) },
                selectedContentColor = Color(0xFFCCC4C4),
                unselectedContentColor = Color(0xFF817C7C),
                selected = currentDestination?.hierarchy?.any { it.route == navItem.screen_route } == true,
                onClick = { gameNavController.navigate(navItem.screen_route) },
            )
        }

    }
}

@Composable
fun GamesTopAppBar(mainNavController: NavHostController) {

    val loginVM = viewModel<LoginViewModel>(LocalContext.current as ComponentActivity)
    val gameOverviewVM = viewModel<GameOverviewViewModel>(LocalContext.current as ComponentActivity)
    val userEmail = loginVM.getUserEmail()

    TopAppBar(
        backgroundColor = Color(0xFF363333),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier.fillMaxSize()
        ) {
            Text(
                text = "Email: ${userEmail ?: "no email available"}",
                color = Color(0xFFF3E8E8),
                modifier = Modifier.padding(start= 5.dp)
            )
            TextButton(onClick = {
                loginVM.signOut()
                mainNavController.navigate(LOGIN_PAGE_ROUTE)
                gameOverviewVM.isLoadingGames.value = true
                gameOverviewVM.isLoadingGameIds.value = true
            }) {
                Text(
                    text = "SIGN OUT",
                    color = Color(0xFFF3E8E8),
                )
            }
        }
    }
}