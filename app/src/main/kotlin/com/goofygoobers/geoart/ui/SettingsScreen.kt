/**
 * Component for the Settings Screen.
 */
package com.goofygoobers.geoart.ui

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
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.goofygoobers.geoart.ScreenNames
import com.goofygoobers.geoart.ui.components.PasswordField
import com.goofygoobers.geoart.ui.components.TopBar
import com.goofygoobers.geoart.viewmodel.SettingsViewModel
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

/**
 * Displays the settings screen, allowing the user to change their password, view the privacy policy
 *
 * @param navController The navigation controller used to navigate between screens.
 * @param modifier The modifier to apply to this layout node.
 * @param viewModel The settings view model to use, injected by Hilt.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    navController: NavHostController,
    modifier: Modifier = Modifier,
    viewModel: SettingsViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
        topBar = {
            TopBar(
                navController = navController,
                canNavigateBack = true,
            )
        },
    ) { innerPadding ->
        Column(modifier = Modifier.padding(innerPadding)) {
            LazyColumn(
                modifier =
                    Modifier
                        .padding(16.dp),
            ) {
                val textModifier: Modifier = Modifier
                val spacerModifier: Modifier = Modifier.height(10.dp)
                item {
                    Text(
                        text = "Settings",
                        modifier = textModifier,
                        style = MaterialTheme.typography.titleLarge,
                        color = MaterialTheme.colorScheme.onBackground,
                    )
                }
                item {
                    Spacer(modifier = spacerModifier)
                }
                item {
                    Box(
                        modifier =
                            Modifier
                                .fillMaxWidth()
                                .border(1.dp, Color.Gray, shape = RoundedCornerShape(8.dp))
                                .padding(16.dp),
                    ) {
                        Row(
                            horizontalArrangement = Arrangement.SpaceBetween,
                            modifier =
                                modifier
                                    .fillMaxWidth(),
                        ) {
                            Text(text = "Private Account")
                            Switch(
                                checked = uiState.isPrivate,
                                onCheckedChange = viewModel::onPrivate,
                            )
                        }
                    }
                }
                item {
                    Spacer(modifier = spacerModifier)
                }
                item {
                    Box(
                        modifier =
                            Modifier
                                .fillMaxWidth()
                                .border(1.dp, Color.Gray, shape = RoundedCornerShape(8.dp))
                                .padding(16.dp),
                    ) {
                        Column {
                            Text(text = "Change Password")
                            Spacer(modifier = spacerModifier)
                            PasswordField(
                                modifier = Modifier.fillMaxWidth(),
                                value = uiState.oldPassword,
                                label = "Old Password",
                                onValueChange = viewModel::onOldPasswordChange,
                            )
                            Spacer(modifier = spacerModifier)
                            PasswordField(
                                modifier = Modifier.fillMaxWidth(),
                                value = uiState.newPassword,
                                label = "New Password",
                                onValueChange = viewModel::onNewPasswordChange,
                            )
                            Spacer(modifier = spacerModifier)
                            Button(
                                onClick = viewModel::updatePassword,
                                enabled = viewModel.updatePasswordEnabled(),
                            ) {
                                Text("Confirm")
                            }
                        }
                    }
                }
                item {
                    Spacer(modifier = spacerModifier)
                }
                item {
                    Box(
                        modifier =
                            Modifier
                                .fillMaxWidth()
                                .border(1.dp, Color.Gray, shape = RoundedCornerShape(8.dp))
                                .padding(16.dp)
                                .clickable {
                                    navController.navigate(ScreenNames.PrivacyPolicy.name)
                                },
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            Text(text = "Review the Privacy Policy")
                            Icon(
                                imageVector = Icons.Filled.ArrowForwardIos,
                                contentDescription = null,
                                modifier = Modifier.size(24.dp),
                            )
                        }
                    }
                }
                item { Spacer(modifier = spacerModifier) }
                item {
                    Box(
                        modifier =
                            Modifier
                                .fillMaxWidth()
                                .border(1.dp, Color.Gray, shape = RoundedCornerShape(8.dp))
                                .padding(16.dp)
                                .clickable {
                                    navController.navigate(ScreenNames.TermsAndConditions.name)
                                },
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            Text(text = "Review the Terms & Conditions of Use")
                            Icon(
                                imageVector = Icons.Filled.ArrowForwardIos,
                                contentDescription = null,
                                modifier = Modifier.size(24.dp),
                            )
                        }
                    }
                }
                item {
                    Row(
                        horizontalArrangement = Arrangement.Center,
                        modifier =
                            modifier
                                .fillMaxWidth()
                                .padding(12.dp),
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
private fun SettingsScreenPreview() {
    SettingsScreen(navController = rememberNavController())
}
