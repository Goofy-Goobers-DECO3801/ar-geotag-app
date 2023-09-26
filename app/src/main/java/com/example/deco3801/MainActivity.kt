package com.example.deco3801

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.deco3801.artdisplay.presentation.ArtDisplayViewModel
import com.example.deco3801.ui.LoginScreen
import com.example.deco3801.ui.SignUpScreen
import com.example.deco3801.ui.theme.DECO3801Theme
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

enum class ScreenNames {
    Login,
    SignUp,
    Screen,
    Home,
    Create,
    Profile,
    TermsAndConditions,
    PrivacyPolicy,
    ARscreen,
    Settings
}

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject
    lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        // TODO("look into dependency injection")
        val virtualTryOnViewModel by viewModels<ArtDisplayViewModel>()

        super.onCreate(savedInstanceState)

        setContent {
            DECO3801Theme {

                val navController = rememberNavController()

                val startDestination = if (auth.currentUser == null) {
                    ScreenNames.Login.name
                } else {
                    ScreenNames.Screen.name
                }

                NavHost(navController = navController, startDestination = startDestination) {
                    composable(ScreenNames.Login.name) {
                        LoginScreen(navController)
                    }
                    composable(ScreenNames.SignUp.name) {
                        SignUpScreen(navController)
                    }
                    composable(ScreenNames.Screen.name) {
                        AppFunctionality(virtualTryOnViewModel)
                    }
                }

            }
        }
    }
}