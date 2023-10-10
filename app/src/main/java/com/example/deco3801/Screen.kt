package com.example.deco3801

import android.util.Log
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.navArgument
import com.example.deco3801.artdisplay.presentation.ArtDisplayScreen
import com.example.deco3801.artdisplay.presentation.ArtDisplayViewModel
import com.example.deco3801.ui.ArtworkNavScreen
import com.example.deco3801.ui.CreateScreen
import com.example.deco3801.ui.EditProfileScreen
import com.example.deco3801.ui.HomeScreen
import com.example.deco3801.ui.LoginScreen
import com.example.deco3801.ui.PrivacyPolicyScreen
import com.example.deco3801.ui.ProfileScreen
import com.example.deco3801.ui.SettingsScreen
import com.example.deco3801.ui.SignUpScreen
import com.example.deco3801.ui.TandCScreen
import com.example.deco3801.ui.components.NavBar
import com.google.firebase.auth.FirebaseUser
import java.net.URLEncoder
import java.nio.charset.StandardCharsets

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppFunctionality(
    artDisplayViewModel: ArtDisplayViewModel,
    authUser: FirebaseUser?,
    appState: AppState = rememberAppState(),
) {
    Log.d("AUTH", authUser.toString())
    val startDestination =
        if (authUser == null) {
            ScreenNames.Login.name
        } else {
            ScreenNames.Home.name
        }
    val navBackStackEntry by appState.navController.currentBackStackEntryAsState()
    val current = navBackStackEntry?.destination?.route

    Scaffold(
        snackbarHost = { SnackbarHost(appState.snackbarHostState) },
        bottomBar = {
            if (current != null && current !in
                listOf(
                    ScreenNames.Login.name,
                    ScreenNames.SignUp.name,
                )
            ) {
                NavBar(appState.navController)
            }
        },
    ) { innerPadding ->
        NavHost(
            navController = appState.navController,
            startDestination = startDestination,
            modifier = Modifier.padding(innerPadding),
        ) {
            composable(ScreenNames.Login.name) {
                LoginScreen(appState.navController)
            }
            composable(ScreenNames.SignUp.name) {
                SignUpScreen(appState.navController)
            }
            composable(route = ScreenNames.Home.name) {
                HomeScreen(appState.navController)
            }
            composable(route = ScreenNames.Create.name) {
                CreateScreen(appState.navController)
            }
            composable(route = "${ScreenNames.Profile.name}/{uid}") {
                val uId = it.arguments?.getString("uid")
                uId?.let { id ->
                    ProfileScreen(id, appState.navController)
                }
            }
            composable(
                route = "${ScreenNames.ARscreen.name}?uri={uri}",
                arguments =
                    listOf(
                        navArgument("uri") {
                            type = NavType.StringType
                        },
                    ),
            ) {
                val uri = it.arguments?.getString("uri") ?: ""

                ArtDisplayScreen(uri, artDisplayViewModel)
            }
            composable(route = ScreenNames.Settings.name) {
                SettingsScreen(appState.navController)
            }
            composable(route = ScreenNames.TermsAndConditions.name) {
                TandCScreen(appState.navController)
            }
            composable(route = ScreenNames.PrivacyPolicy.name) {
                PrivacyPolicyScreen(appState.navController)
            }
            composable(route = ScreenNames.EditProfile.name) {
                EditProfileScreen(appState.navController)
            }
            composable(route = "${ScreenNames.ArtworkNav.name}/{id}") {
                val id = it.arguments?.getString("id")
                if (id != null) {
                    ArtworkNavScreen(id, appState.navController)
                }
            }
        }
    }
}

fun NavHostController.navigateAR(uri: String) {
    val encondedUri = URLEncoder.encode(uri, StandardCharsets.UTF_8.toString())
    return this.navigate("${ScreenNames.ARscreen.name}?uri=$encondedUri")
}

fun NavHostController.navigateArt(artId: String) {
    return this.navigate("${ScreenNames.ArtworkNav.name}/$artId")
}

fun NavHostController.navigateProfile(userId: String) {
    return this.navigate("${ScreenNames.Profile.name}/$userId")
}
