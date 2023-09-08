package com.example.deco3801.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Create
import androidx.compose.material.icons.filled.Home
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.example.deco3801.ScreenNames
import com.example.deco3801.ui.theme.MyColors

@Composable
fun NavButton(text: String, icon: ImageVector, visitPage: () -> Unit, isSelected: Boolean) {
    val backgroundColor = if (isSelected) {
        MyColors.Orange
    } else {
        Color.White
    }

    val contentColor = if (isSelected) {
        Color.White
    } else {
        MyColors.Orange
    }

    Button(
        onClick = visitPage,
        colors = ButtonDefaults.buttonColors(
            containerColor = backgroundColor,
            contentColor = contentColor
        ),
        modifier = Modifier
            .padding(
                top = 10.dp,
                bottom = 10.dp
            )
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Icon(imageVector = icon, contentDescription = text)
            Text(text = text, style = MaterialTheme.typography.labelSmall)
        }
    }
}

//@Preview
@Composable
fun NavBar(navController: NavHostController) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(color = MyColors.Grey),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        NavButton(
            text = "Home",
            Icons.Default.Home,
            { navController.navigate(ScreenNames.Home.name) },
            isSelected = currentRoute == ScreenNames.Home.name
        )
        NavButton(
            text = "Create",
            Icons.Default.Create,
            { navController.navigate(ScreenNames.Create.name) },
            isSelected = currentRoute == ScreenNames.Create.name
        )
        NavButton(
            text = "Profile",
            Icons.Default.AccountCircle,
            { navController.navigate(ScreenNames.Profile.name) },
            isSelected = currentRoute == ScreenNames.Profile.name
        )
    }
}