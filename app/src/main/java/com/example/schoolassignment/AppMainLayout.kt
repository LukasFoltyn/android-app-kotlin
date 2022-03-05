package com.example.schoolassignment

import androidx.compose.runtime.Composable
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

const val LOGIN_PAGE_ROUTE = "loginPageView"
const val SCAFFOLD_GAME_PAGE_ROUTE = "scaffoldPageView"
const val SCAFFOLD_GAME_DETAIL_ROUTE = "scaffoldGameDetailView"


@Composable
fun AppMainLayout() {

    val mainNavController = rememberNavController()

    NavHost(navController = mainNavController, startDestination = SCAFFOLD_GAME_PAGE_ROUTE) {
        composable(route = LOGIN_PAGE_ROUTE) {
            LoginPageView(mainNavController)
        }
        composable(route = SCAFFOLD_GAME_PAGE_ROUTE)
        {
            GamePageView(mainNavController)
        }
        composable(route = "$SCAFFOLD_GAME_DETAIL_ROUTE/{gameId}")
        { backStackEntry ->
            GameDetailView(mainNavController, backStackEntry.arguments!!.getString("gameId")!!)
        }
    }
}