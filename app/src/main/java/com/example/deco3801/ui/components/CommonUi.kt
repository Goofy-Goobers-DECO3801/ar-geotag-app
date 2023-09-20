package com.example.deco3801.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowBackIos
import androidx.compose.material.icons.filled.Create
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.deco3801.R
import com.example.deco3801.ScreenNames
import com.example.deco3801.ui.theme.UnchangingAppColors

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopBar(
    canNavigateBack: Boolean,
    showSettings: Boolean,
    navigateUp: () -> Unit
) {
    TopAppBar(
        title = {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                if (!canNavigateBack) {
                    Image(
                        painter = painterResource(id = R.drawable.logo),
                        contentDescription = "Logo",
                        modifier = Modifier.size(32.dp)
                    )
                }
            }
        },
        colors = TopAppBarDefaults.mediumTopAppBarColors(
            titleContentColor = Color.White,
            containerColor = UnchangingAppColors.main_theme
        ),
        navigationIcon = {
            if (canNavigateBack) {
                IconButton(onClick = navigateUp) {
                    Icon(
                        imageVector = Icons.Filled.ArrowBackIos,
                        contentDescription = "ArrowBack"
                    )
                }
            }
        },
        actions = {
            if (showSettings) {
                IconButton(onClick = navigateUp) {
                    Icon(
                        imageVector = Icons.Filled.Menu,
                        contentDescription = "Settings",
                        tint = Color.White,
                        modifier = Modifier.size(36.dp)
                    )
                }
            }
        }
    )
}

@Composable
fun NavButton(text: String, icon: ImageVector, visitPage: () -> Unit, isSelected: Boolean) {
    val backgroundColor = if (isSelected) {
        Color.White
    } else {
        UnchangingAppColors.main_theme
    }

    val contentColor = if (isSelected) {
        UnchangingAppColors.main_theme
    } else {
        Color.White
    }

    Button(
        onClick = visitPage,
        colors = ButtonDefaults.buttonColors(
            containerColor = backgroundColor,
            contentColor = contentColor
        ),
        modifier = Modifier
            .padding(
                top = 7.dp,
                bottom = 7.dp
            ),
        shape = RoundedCornerShape(5.dp)
    ) {
        Column(
            modifier = Modifier.width(60.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(imageVector = icon, contentDescription = text)
            Text(text = text, style = MaterialTheme.typography.labelSmall)
        }
    }
}

@Composable
fun NavBar(navController: NavHostController) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(UnchangingAppColors.main_theme),
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

@Preview
@Composable
fun PreviewNavBar() {
    NavBar(navController = rememberNavController())
}
