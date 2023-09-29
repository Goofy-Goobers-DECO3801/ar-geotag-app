package com.example.deco3801

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import com.chaquo.python.Python
import com.chaquo.python.android.AndroidPlatform
import com.example.deco3801.artdisplay.presentation.ArtDisplayViewModel
import com.example.deco3801.ui.theme.DECO3801Theme
import dagger.hilt.android.AndroidEntryPoint

enum class ScreenNames {
    Login,
    SignUp,
    Home,
    Create,
    Profile,
    TermsAndConditions,
    PrivacyPolicy,
    ARscreen,
    Settings,
    EditProfile
}

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        // TODO("look into dependency injection")

        val virtualTryOnViewModel by viewModels<ArtDisplayViewModel>()

        if (!Python.isStarted()) {
            Python.start(AndroidPlatform(this));
        }

        super.onCreate(savedInstanceState)

        setContent {
            DECO3801Theme {
                AppFunctionality(virtualTryOnViewModel)
            }
        }
    }
}