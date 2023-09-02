package com.example.deco3801

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
import androidx.compose.material3.ExperimentalMaterial3Api
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.deco3801.ui.theme.MyColors

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UserInput (inputName: String, initialValue: String) {
    var inputValue by remember { mutableStateOf(initialValue) }

    Column (
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 30.dp,
                end = 30.dp,
                top = 10.dp,
                bottom = 10.dp)
    ) {
        Text(
            text = inputName,
            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
            color = Color.White
        )
        Spacer(modifier = Modifier.height(10.dp))
        TextField(
            modifier = Modifier.fillMaxWidth(),
            value = inputValue,
            onValueChange = {newValue -> inputValue = newValue},
        )
    }
}
@Composable
fun LoginScreen(navController: NavHostController) {
    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

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
        UserInput(inputName = "Username or email", initialValue = username)
        UserInput(inputName = "Password", initialValue = password)

        Spacer(modifier = Modifier.height(20.dp))

        Button(onClick = { navController.navigate(ScreenNames.Screen.name) }) {
            Text("Sign In", fontSize = 23.sp)
        }

        Button(
            onClick = { navController.navigate(ScreenNames.SignUp.name) },
            modifier = Modifier.padding(10.dp)
        ) {
            Text("Don't have an account? Sign Up")
        }
    }
}