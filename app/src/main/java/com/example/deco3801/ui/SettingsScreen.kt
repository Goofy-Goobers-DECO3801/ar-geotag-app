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
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.deco3801.ui.theme.MyColors
import java.lang.Boolean.TRUE

@Composable
fun SettingsScreen(modifier : Modifier = Modifier) {
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
                color = MyColors.DarkOrange
            )
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
                        /*TODO*/
                    }
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(text = "Review the Privacy Policy")
                    Icon(
                        imageVector = Icons.Default.ArrowForward,
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
                        /*TODO*/
                    }
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(text = "Review the Terms & Conditions of Use")
                    Icon(
                        imageVector = Icons.Default.ArrowForward,
                        contentDescription = null,
                        modifier = Modifier.size(24.dp)
                    )
                }
            }
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
                    //TextField(value = "Enter New Password", onValueChange = viewModel::onPasswordChange)
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
                Button(onClick = { /*TODO*/ }) {
                    Text(text = "Sign Out")
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun SettingsScreenPreview() {
    SettingsScreen()
}