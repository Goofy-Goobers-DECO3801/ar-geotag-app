package com.example.deco3801

import androidx.compose.foundation.layout.padding
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
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.deco3801.ui.CreateScreen
import com.example.deco3801.ui.FavouritesScreen
import com.example.deco3801.ui.SettingsScreen
import com.example.deco3801.ui.HomeScreen
import com.example.deco3801.ui.components.NavBar
import com.example.deco3801.ui.theme.MyColors

enum class ScreenNames() {
    Home,
    Create,
    Favourites,
    Settings
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopBar(
    canNavigateBack: Boolean,
    navigateUp: () -> Unit,
    modifier: Modifier = Modifier
) {
    TopAppBar(
        title = { Text("Goofy Goobers") },
        colors = TopAppBarDefaults.mediumTopAppBarColors(
            titleContentColor = Color.White,
            containerColor = MyColors.Orange
        ),
        modifier = modifier,
        navigationIcon = {
            if (canNavigateBack) {
                IconButton(onClick = navigateUp) {
                    Icon(
                        imageVector = Icons.Filled.ArrowBack,
                        contentDescription = "ArrowBack"
                    )
                }
            }
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppFunctionality(
    navController: NavHostController = rememberNavController()
) {
    Scaffold(
        topBar = {
            TopBar(
                canNavigateBack = false,
                navigateUp = { /* TODO: implement back navigation */ }
            )
        },
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
                CreateScreen()
            }
            composable(route = ScreenNames.Favourites.name) {
                FavouritesScreen()
            }
            composable(route = ScreenNames.Settings.name) {
                SettingsScreen()
            }
        }
    }
}