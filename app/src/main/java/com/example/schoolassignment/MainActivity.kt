package com.example.schoolassignment

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.MaterialTheme
import androidx.compose.material.OutlinedButton
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.schoolassignment.ui.theme.SchoolAssignmentTheme
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            SchoolAssignmentTheme {
                // A surface container using the 'background' color from the theme
                Surface(color = MaterialTheme.colors.background) {
                    AppLayout()
                }
            }
        }
    }
}

const val LOGIN_PAGE_ROUTE = "loginPageView"
const val APP_PAGE_ROUTE = "appPageView"

@Composable
fun AppLayout() {

    val navController = rememberNavController()
    val firebaseAuth = Firebase.auth

    NavHost(navController = navController, startDestination = LOGIN_PAGE_ROUTE) {
        composable(route = LOGIN_PAGE_ROUTE) {
            LoginPageView(navController)
        }
        composable(route = APP_PAGE_ROUTE)
        {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Blue),
                contentAlignment = Alignment.Center
            ) {
                OutlinedButton(onClick = {
                    firebaseAuth.signOut()
                    navController.navigate(LOGIN_PAGE_ROUTE)
                }) {
                    Text(text = "SIGN OUT")
                }
            }
        }
    }
}


