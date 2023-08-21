package com.example.deco3801.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Create
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.deco3801.ScreenNames
import com.example.deco3801.ui.theme.MyColors

@Composable
fun NavButton (text: String, icon: ImageVector, visitPage: () -> Unit, isSelected: Boolean) {
    Button(
        onClick = visitPage,
        colors = ButtonDefaults.buttonColors(
            containerColor = MyColors.Orange
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
            isSelected = true
        )
        NavButton(
            text = "Create",
            Icons.Default.Create,
            { navController.navigate(ScreenNames.Create.name) },
            isSelected = false
        )
        NavButton(
            text = "Favorites",
            Icons.Default.Favorite,
            { navController.navigate(ScreenNames.Favourites.name) },
            isSelected = false
        )
        NavButton(
            text = "Settings",
            Icons.Default.Settings,
            { navController.navigate(ScreenNames.Settings.name) },
            isSelected = false
        )
    }
}