/**
 * Contains common UI components used throughout the app.
 */
package com.example.deco3801.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.ArrowBackIos
import androidx.compose.material.icons.filled.Create
import androidx.compose.material.icons.filled.Home
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.deco3801.R
import com.example.deco3801.ScreenNames
import com.example.deco3801.ui.theme.UnchangingAppColors
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

/**
 * A composable top bar that is used throughout the app.
 *
 * @param navController The navController used to navigate between screens.
 * @param modifier The modifier to be applied to the top bar.
 * @param title The title of the top bar.
 * @param canNavigateBack Whether the top bar should have a back button.
 * @param actions The actions to be displayed on the top bar.
 * @see [TopAppBar]
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopBar(
    navController: NavHostController,
    modifier: Modifier = Modifier,
    title: String = "",
    canNavigateBack: Boolean = false,
    actions: @Composable() (RowScope.() -> Unit) = {},
) {
    TopAppBar(
        title = {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center,
            ) {
                AutoSizeText(
                    text = title,
                    style = MaterialTheme.typography.headlineLarge,
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center,
                    maxLines = 1,
                    softWrap = false,
                    overflow = TextOverflow.Clip,
                )
            }
        },
        colors =
            TopAppBarDefaults.mediumTopAppBarColors(
                titleContentColor = Color.White,
                containerColor = UnchangingAppColors.main_theme,
            ),
        navigationIcon = {
            if (canNavigateBack) {
                IconButton(onClick = navController::popBackStack) {
                    Icon(
                        imageVector = Icons.Filled.ArrowBackIos,
                        contentDescription = "ArrowBack",
                        tint = Color.White,
                    )
                }
            } else {
                IconButton(onClick = { navController.navigate(ScreenNames.Home.name) }) {
                    Image(
                        painter = painterResource(id = R.drawable.logo),
                        contentDescription = "Logo",
                        modifier = Modifier.size(32.dp),
                    )
                }
            }
        },
        actions = actions,
        modifier = modifier,
    )
}

/**
 * A composable text box that automatically resizes the text to fit the constrained width.
 *
 * @see [Text] for parameters.
 * @reference
 * B. Hoffmann, "android:autoSizeTextType in Jetpack Compose," Stackoverflow, 7 July 2021.
 * \[Online]. Available: https://stackoverflow.com/a/66090448. [Accessed 16 October 2023].
 */
@Composable
fun AutoSizeText(
    text: String,
    modifier: Modifier = Modifier,
    color: Color = Color.Unspecified,
    fontSize: TextUnit = TextUnit.Unspecified,
    fontStyle: FontStyle? = null,
    fontWeight: FontWeight? = null,
    fontFamily: FontFamily? = null,
    letterSpacing: TextUnit = TextUnit.Unspecified,
    textDecoration: TextDecoration? = null,
    textAlign: TextAlign? = null,
    lineHeight: TextUnit = TextUnit.Unspecified,
    overflow: TextOverflow = TextOverflow.Clip,
    softWrap: Boolean = true,
    maxLines: Int = Int.MAX_VALUE,
    minLines: Int = 1,
    style: TextStyle = LocalTextStyle.current,
) {
    var textStyle by remember { mutableStateOf(style) }
    var readyToDraw by remember { mutableStateOf(false) }
    Text(
        text = text,
        color = color,
        fontSize = fontSize,
        fontStyle = fontStyle,
        fontWeight = fontWeight,
        fontFamily = fontFamily,
        letterSpacing = letterSpacing,
        textDecoration = textDecoration,
        textAlign = textAlign,
        lineHeight = lineHeight,
        overflow = overflow,
        softWrap = softWrap,
        maxLines = maxLines,
        minLines = minLines,
        style = textStyle,
        modifier =
            modifier.drawWithContent {
                if (readyToDraw) drawContent()
            },
        onTextLayout = { textLayoutResult ->
            if (textLayoutResult.didOverflowWidth) {
                textStyle = textStyle.copy(fontSize = textStyle.fontSize * 0.9)
            } else {
                readyToDraw = true
            }
        },
    )
}

/**
 * A composable button used on the bottom navigation bar.
 *
 * @param text The text to be displayed on the button.
 * @param icon The icon to be displayed on the button.
 * @param visitPage The function to be called when the button is clicked.
 * @param isSelected Whether the button is selected.
 */
@Composable
fun NavButton(
    text: String,
    icon: ImageVector,
    visitPage: () -> Unit,
    isSelected: Boolean,
) {
    val backgroundColor =
        if (isSelected) {
            UnchangingAppColors.lighter_main_theme
        } else {
            MaterialTheme.colorScheme.background
        }

    val contentColor =
        if (isSelected) {
            UnchangingAppColors.main_theme
        } else {
            MaterialTheme.colorScheme.onBackground
        }

    Button(
        onClick = visitPage,
        colors =
            ButtonDefaults.buttonColors(
                containerColor = backgroundColor,
                contentColor = contentColor,
            ),
        contentPadding = PaddingValues(top = 4.dp, bottom = 4.dp, start = 25.dp, end = 25.dp),
        modifier =
            Modifier
                .padding(
                    top = 7.dp,
                    bottom = 7.dp,
                ),
        shape = RoundedCornerShape(5.dp),
    ) {
        Column(
            modifier = Modifier.width(60.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Icon(imageVector = icon, contentDescription = text)
            Text(text = text, style = MaterialTheme.typography.labelSmall)
        }
    }
}

/**
 * The bottom navigation bar for the app.
 * The nav bar is used to navigate between the home, create and profile screens.
 *
 * @param navController The navController used to navigate between screens.
 */
@Composable
fun NavBar(navController: NavHostController) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route
    val progressState by ProgressbarState.state.collectAsState()

    Column(modifier = Modifier.fillMaxWidth()) {
        if (progressState.visible) {
            if (progressState.progress < 0) {
                LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
            } else {
                LinearProgressIndicator(
                    modifier = Modifier.fillMaxWidth(),
                    progress = progressState.progress,
                )
            }
        }
        Spacer(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .height((0.5).dp)
                    .background(MaterialTheme.colorScheme.onError),
        )
        Row(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.background),
            horizontalArrangement = Arrangement.SpaceEvenly,
        ) {
            NavButton(
                text = "Home",
                Icons.Default.Home,
                { navController.navigate(ScreenNames.Home.name) },
                isSelected = currentRoute == ScreenNames.Home.name,
            )
            NavButton(
                text = "Create",
                Icons.Default.Create,
                { navController.navigate(ScreenNames.Create.name) },
                isSelected = currentRoute == ScreenNames.Create.name,
            )
            NavButton(
                text = "Profile",
                Icons.Default.AccountCircle,
                { navController.navigate("${ScreenNames.Profile.name}/${Firebase.auth.uid}") },
                isSelected =
                    if (currentRoute == "${ScreenNames.Profile.name}/{uid}") {
                        val uId = navBackStackEntry?.arguments?.getString("uid")
                        uId == Firebase.auth.uid
                    } else {
                        false
                    },
            )
        }
    }
}

@Preview
@Composable
private fun PreviewNavBar() {
    NavBar(rememberNavController())
}
