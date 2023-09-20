package com.example.deco3801

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.deco3801.artdisplay.presentation.ArtDisplayScreen
import com.example.deco3801.artdisplay.presentation.ArtDisplayViewModel
import com.example.deco3801.ui.CreateScreen
import com.example.deco3801.ui.ProfileScreen
import com.example.deco3801.ui.HomeScreen
import com.example.deco3801.ui.components.NavBar
import com.example.deco3801.ui.components.TopBar


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppFunctionality(
    ArtDisplayViewModel: ArtDisplayViewModel,
    navController: NavHostController = rememberNavController()
) {
    Scaffold(
        bottomBar = {
            NavBar(navController)
        }
    ) { innerPadding ->

        NavHost(
            navController = navController,
            startDestination = ScreenNames.Home.name,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(route = ScreenNames.Home.name) {
                HomeScreen()
            }
            composable(route = ScreenNames.Create.name) {
                CreateScreen(navController)
            }
            composable(route = ScreenNames.Profile.name) {
                ProfileScreen()
            }
            composable(route = ScreenNames.ARscreen.name) {
                ArtDisplayScreen(0, ArtDisplayViewModel)
            }
        }
    }
}