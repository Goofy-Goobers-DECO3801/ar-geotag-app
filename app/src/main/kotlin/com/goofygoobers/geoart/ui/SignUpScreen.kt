/**
 * Compose UI for the sign up screen.
 */
package com.goofygoobers.geoart.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.ClickableText
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.goofygoobers.geoart.R
import com.goofygoobers.geoart.ScreenNames
import com.goofygoobers.geoart.ui.components.EmailField
import com.goofygoobers.geoart.ui.components.NameField
import com.goofygoobers.geoart.ui.components.PasswordField
import com.goofygoobers.geoart.ui.theme.UnchangingAppColors
import com.goofygoobers.geoart.viewmodel.AuthViewModel

/**
 * Displays the sign up screen, allowing the user to sign up to the app and create an account.
 *
 * @param navController The navigation controller to use.
 * @param viewModel The auth view model to use, injected by Hilt.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SignUpScreen(
    navController: NavHostController,
    viewModel: AuthViewModel = hiltViewModel(),
) {
    val uiState = viewModel.uiState
    var isChecked by remember { mutableStateOf(false) }
    var viewTandC by remember { mutableStateOf(false) }
    var viewPrivacyPolicy by remember { mutableStateOf(false) }

    Column(
        modifier =
            Modifier
                .fillMaxSize()
                .background(color = UnchangingAppColors.main_theme),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Image(
            painter = painterResource(id = R.drawable.logo),
            contentDescription = "Logo",
            modifier = Modifier.size(150.dp),
        )

        NameField(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .padding(
                        start = 30.dp,
                        end = 30.dp,
                        top = 30.dp,
                        bottom = 10.dp,
                    ),
            value = uiState.username,
            onValueChange = viewModel::onUsernameChange,
        )

        EmailField(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .padding(
                        start = 30.dp,
                        end = 30.dp,
                        top = 10.dp,
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

        Row(
            modifier =
                Modifier.padding(
                    start = 30.dp,
                    end = 30.dp,
                    top = 0.dp,
                    bottom = 20.dp,
                ),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Checkbox(
                checked = isChecked,
                onCheckedChange = { isChecked = it },
            )
            val agreementText = "I agree to Terms and Conditions and Privacy Policy"
            ClickableText(
                text = AnnotatedString(agreementText),
                onClick = { offset ->
                    if (offset >= agreementText.indexOf("Terms and Conditions") &&
                        offset < agreementText.indexOf("Terms and Conditions") + "Terms and Conditions".length
                    ) {
                        viewTandC = true
                    } else if (offset >= agreementText.indexOf("Privacy Policy") &&
                        offset < agreementText.indexOf("Privacy Policy") + "Privacy Policy".length
                    ) {
                        viewPrivacyPolicy = true
                    }
                },
                style = TextStyle(fontSize = 15.sp, color = Color.White),
            )
        }

        if (viewTandC) {
            TermsAndConditionsDialog { viewTandC = false }
        }

        if (viewPrivacyPolicy) {
            PrivacyPolicyDialog { viewPrivacyPolicy = false }
        }

        Button(onClick = {
            viewModel.onSignUpClicked(
                open = {
                    navController.navigate(it)
                },
            )
        }) {
            Text("Sign Up", fontSize = 23.sp)
        }

        Button(
            onClick = { navController.navigate(ScreenNames.Login.name) },
            modifier = Modifier.padding(10.dp),
        ) {
            Text("Already have an account? Log in")
        }
    }
}
