package com.example.deco3801.ui

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.ClickableText
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.example.deco3801.R
import com.example.deco3801.ScreenNames
import com.example.deco3801.ui.components.EmailField
import com.example.deco3801.ui.components.PasswordField
import com.example.deco3801.ui.theme.UnchangingAppColors
import com.example.deco3801.viewmodel.AuthViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SignUpScreen(
    navController: NavHostController,
    viewModel: AuthViewModel = hiltViewModel(),
) {
    val uiState = viewModel.uiState
    val context = LocalContext.current
    var isChecked by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var viewTandC by remember { mutableStateOf(false) }
    var viewPrivacyPolicy by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(color = UnchangingAppColors.main_theme),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Image(
            painter = painterResource(id = R.drawable.logo),
            contentDescription = "Logo",
            modifier = Modifier.size(150.dp)
        )

        TextField(
            modifier = Modifier
                .fillMaxWidth()
                .padding(
                    start = 30.dp,
                    end = 30.dp,
                    top = 30.dp,
                    bottom = 10.dp
                ),
            value = uiState.username,
            onValueChange = viewModel::onUsernameChange,
            label = { Text("Username") },
            singleLine = true,
        )

        EmailField(
            modifier = Modifier
                .fillMaxWidth()
                .padding(
                    start = 30.dp,
                    end = 30.dp,
                    top = 10.dp,
                    bottom = 10.dp
                ),
            value = uiState.email,
            onValueChange = viewModel::onEmailChange
        )

        PasswordField(
            modifier = Modifier
                .fillMaxWidth()
                .padding(
                    start = 30.dp,
                    end = 30.dp,
                    top = 10.dp,
                    bottom = 20.dp
                ),
            value = uiState.password,
            onValueChange = viewModel::onPasswordChange
        )

        Row(
            modifier = Modifier.padding(
                start = 30.dp,
                end = 30.dp,
                top = 0.dp,
                bottom = 20.dp
            ),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Checkbox(
                checked = isChecked,
                onCheckedChange = { isChecked = it }
            )
            val agreementText = "I agree to Terms and Conditions and Privacy Policy"
            ClickableText(
                text = AnnotatedString(agreementText),
                onClick = { offset ->
                    if (offset >= agreementText.indexOf("Terms and Conditions") &&
                        offset < agreementText.indexOf("Terms and Conditions") + "Terms and Conditions".length) {
                        viewTandC = true
                    } else if (offset >= agreementText.indexOf("Privacy Policy") &&
                        offset < agreementText.indexOf("Privacy Policy") + "Privacy Policy".length) {
                        viewPrivacyPolicy = true
                    }
                },
                style = TextStyle(fontSize = 15.sp, color = Color.White)
            )
        }

        if (viewTandC) {
            TandCDialog { viewTandC = false }
        }

        if (viewPrivacyPolicy) {
            PrivacyPolicyDialog { viewPrivacyPolicy = false }
        }

        Button(onClick = {
            viewModel.onSignUpClicked(
                onSuccess = {
                    navController.navigate(ScreenNames.Screen.name)
                },
                onFail = {
                    Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
                }
            )
        }) {
            Text("Sign Up", fontSize = 23.sp)
        }

        Button(
            onClick = { navController.navigate(ScreenNames.Login.name) },
            modifier = Modifier.padding(10.dp)
        ) {
            Text("Already have an account? Log in")
        }
    }
}
