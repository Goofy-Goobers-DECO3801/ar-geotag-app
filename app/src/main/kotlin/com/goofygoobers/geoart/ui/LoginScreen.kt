/**
 * Composable components for the login screen.
 */
package com.goofygoobers.geoart.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.goofygoobers.geoart.R
import com.goofygoobers.geoart.ScreenNames
import com.goofygoobers.geoart.ui.components.EmailField
import com.goofygoobers.geoart.ui.components.PasswordField
import com.goofygoobers.geoart.ui.theme.UnchangingAppColors
import com.goofygoobers.geoart.viewmodel.AuthViewModel

/**
 * Displays the login screen, allowing the user to login to the app and authenticate themselves.
 *
 * @param navController The navigation controller to use.
 * @param viewModel The auth view model to use, injected by Hilt.
 */
@Composable
fun LoginScreen(
    navController: NavHostController,
    viewModel: AuthViewModel = hiltViewModel(),
) {
    val uiState = viewModel.uiState

    Column(
        modifier =
            Modifier
                .fillMaxSize()
                .background(UnchangingAppColors.main_theme),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Image(
            painter = painterResource(id = R.drawable.logo),
            contentDescription = "Logo",
            modifier = Modifier.size(150.dp),
        )

        EmailField(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .padding(
                        start = 30.dp,
                        end = 30.dp,
                        top = 30.dp,
                        bottom = 10.dp,
                    ),
            value = uiState.email,
            onValueChange = viewModel::onEmailChange,
        )
        PasswordField(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .padding(
                        start = 30.dp,
                        end = 30.dp,
                        top = 10.dp,
                        bottom = 20.dp,
                    ),
            value = uiState.password,
            onValueChange = viewModel::onPasswordChange,
        )

        Spacer(modifier = Modifier.height(20.dp))

        Button(
            onClick = {
                viewModel.onLoginClicked(
                    open = { navController.navigate(it) },
                )
            },
        ) {
            Text("Sign In", fontSize = 23.sp)
        }

        Button(
            onClick = { navController.navigate(ScreenNames.SignUp.name) },
            modifier = Modifier.padding(10.dp),
        ) {
            Text("Don't have an account? Sign Up")
        }
    }
}

@Preview
@Composable
private fun PreviewLoginScreen() {
    LoginScreen(navController = rememberNavController())
}
