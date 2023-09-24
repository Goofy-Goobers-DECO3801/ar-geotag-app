package com.example.deco3801.ui

import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForwardIos
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.example.deco3801.ScreenNames
import com.example.deco3801.ui.components.TopBar
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import java.lang.Boolean.TRUE

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(navController: NavHostController, modifier : Modifier = Modifier) {
    val textFieldModifier: Modifier = Modifier.fillMaxWidth()

    Scaffold(
        topBar = {
            TopBar(
                canNavigateBack = true,
                showSettings = false,
                navigateUp = {navController.navigate(ScreenNames.Profile.name)}
            )
        }
    ) { innerPadding ->
        Column(modifier = Modifier.padding(innerPadding)) {
            LazyColumn(
                modifier = Modifier
                    .padding(16.dp)
            ) {
                val isPrivate = TRUE //TODO viewModel.isPrivateAccountState
                val textModifier: Modifier = Modifier
                val spacerModifier: Modifier = Modifier.height(10.dp)
                item {
                    Text(
                        text = "Settings",
                        modifier = textModifier,
                        style = MaterialTheme.typography.titleLarge,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                }
                item{
                    Spacer(modifier = spacerModifier)
                }
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .border(1.dp, Color.Gray, shape = RoundedCornerShape(8.dp))
                            .padding(16.dp)
                    ) {
                        Row(
                            horizontalArrangement = Arrangement.SpaceBetween,
                            modifier = modifier
                                .fillMaxWidth()
                        ) {
                            Text(text = "Private Account")
                            Switch(
                                checked = isPrivate,
                                onCheckedChange = null
                            )
                        }
                    }
                }
                item {
                    Spacer(modifier = spacerModifier)
                }
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .border(1.dp, Color.Gray, shape = RoundedCornerShape(8.dp))
                            .padding(16.dp)
                    ) {
                        Column() {
                            Text(text = "Change Password")
                            //TODO TextField(value = "Enter New Password", onValueChange = viewModel::onPasswordChange)
                            //PasswordField(value = "Enter a New Password", onValueChange = {})
                        }
                    }
                }
                item {
                    Spacer(modifier = spacerModifier)
                }
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .border(1.dp, Color.Gray, shape = RoundedCornerShape(8.dp))
                            .padding(16.dp)
                            .clickable {
                                navController.navigate(ScreenNames.PrivacyPolicy.name)
                            }
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(text = "Review the Privacy Policy")
                            Icon(
                                imageVector = Icons.Filled.ArrowForwardIos,
                                contentDescription = null,
                                modifier = Modifier.size(24.dp)
                            )
                        }
                    }
                }
                item { Spacer(modifier = spacerModifier) }
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .border(1.dp, Color.Gray, shape = RoundedCornerShape(8.dp))
                            .padding(16.dp)
                            .clickable {
                                navController.navigate(ScreenNames.TermsAndConditions.name)
                            }
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(text = "Review the Terms & Conditions of Use")
                            Icon(
                                imageVector = Icons.Filled.ArrowForwardIos,
                                contentDescription = null,
                                modifier = Modifier.size(24.dp)
                            )
                        }
                    }
                }
                item {
                    Row(
                        horizontalArrangement = Arrangement.Center,
                        modifier = modifier
                            .fillMaxWidth()
                            .padding(12.dp)
                    ) {
                        Button(onClick = {
                            Firebase.auth.signOut()
                            navController.navigate(ScreenNames.Login.name)
                        }) {
                            Text(text = "Sign Out")
                        }
                    }
                }
            }
        }
    }
}


@Preview(showBackground = true)
@Composable
fun SettingsScreenPreview() {
    SettingsScreen(navController = rememberNavController())
}