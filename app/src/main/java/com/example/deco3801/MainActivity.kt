package com.example.deco3801

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import com.chaquo.python.Python
import com.chaquo.python.android.AndroidPlatform
import com.example.deco3801.artdisplay.presentation.ArtDisplayViewModel
import com.example.deco3801.ui.resizeBitmap
import com.example.deco3801.ui.theme.DECO3801Theme
import com.google.firebase.auth.FirebaseUser
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject
import javax.inject.Provider

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
    EditProfile,
    ArtworkNav,
}

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject
    lateinit var authUser: Provider<FirebaseUser?>


    override fun onCreate(savedInstanceState: Bundle?) {
        // TODO("look into dependency injection")

        val artDisplayViewModel by viewModels<ArtDisplayViewModel>()
        val markerIcon =  resizeBitmap(this, 60, 96)

        if (!Python.isStarted()) {
            Python.start(AndroidPlatform(this));
        }

        super.onCreate(savedInstanceState)

        setContent {
            DECO3801Theme {
                AppFunctionality(artDisplayViewModel, authUser = authUser.get(), markerIcon)
            }
        }
    }
}
