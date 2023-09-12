package com.example.deco3801

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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.example.deco3801.ui.PrivacyPolicyDialog
import com.example.deco3801.ui.TandCDialog
import com.example.deco3801.ui.theme.MyColors
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SignUpScreen(navController: NavHostController) {
    var username by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var isPasswordVisible by remember { mutableStateOf(false) }
    var isChecked by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var viewTandC by remember { mutableStateOf(false) }
    var viewPrivacyPolicy by remember { mutableStateOf(false) }

    Column (
        modifier = Modifier
            .fillMaxSize()
            .background(color = MyColors.Orange),
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
            value = username,
            onValueChange = {newUsername -> username = newUsername},
            label = { Text("Username") }
        )

        TextField(
            modifier = Modifier
                .fillMaxWidth()
                .padding(
                    start = 30.dp,
                    end = 30.dp,
                    top = 10.dp,
                    bottom = 10.dp
                ),
            value = email,
            onValueChange = {newEmail -> email = newEmail},
            label = { Text("Email") }
        )

        TextField(
            modifier = Modifier
                .fillMaxWidth()
                .padding(
                    start = 30.dp,
                    end = 30.dp,
                    top = 10.dp,
                    bottom = 20.dp
                ),
            value = password,
            onValueChange = {newPassword -> password = newPassword},
            label = { Text("Password") },
            visualTransformation = if (isPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
            trailingIcon = {
                IconButton(
                    onClick = { isPasswordVisible = !isPasswordVisible }
                ) {
                    val icon = if (isPasswordVisible) Icons.Default.Check else Icons.Default.Lock
                    Icon(imageVector = icon, contentDescription = null)
                }
            }
        )

        Row (
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

        errorMessage?.let {
            Text(
                text = it,
                color = Color.Black,
                fontSize = 15.sp,
                modifier = Modifier.padding(
                    start = 30.dp,
                    end = 30.dp,
                    bottom = 20.dp
                ),
                textAlign = TextAlign.Center,
                fontWeight = FontWeight.Bold
            )
        }

        Button(onClick = {
            if (isChecked) {
                navController.navigate(ScreenNames.Screen.name)
            } else {
                // Display an error message
                errorMessage = "* Please agree to the Terms and Conditions"
            }
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

@Preview
@Composable
fun PreviewSignUp() {
    val navController: NavHostController = rememberNavController()
    SignUpScreen(navController = navController)
}