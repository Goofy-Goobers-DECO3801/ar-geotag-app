package com.example.deco3801

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.ui.graphics.BlendMode.Companion.Screen
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.deco3801.ui.theme.DECO3801Theme

enum class ScreenNames() {
    Login,
    SignUp,
    Screen,
    Home,
    Create,
    Profile
}

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            DECO3801Theme {

                val navController = rememberNavController()

                NavHost(navController = navController, startDestination = ScreenNames.Login.name) {
                    composable(ScreenNames.Login.name) {
                        LoginScreen(navController)
                    }
                    composable(ScreenNames.SignUp.name) {
                        SignUpScreen(navController)
                    }
                    composable(ScreenNames.Screen.name) {
                        AppFunctionality()
                    }
                }

            }
        }
    }
