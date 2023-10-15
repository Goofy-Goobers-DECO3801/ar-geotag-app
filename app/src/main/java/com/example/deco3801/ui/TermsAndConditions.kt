package com.example.deco3801.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.example.deco3801.R
import com.example.deco3801.ScreenNames
import com.example.deco3801.ui.components.TopBar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TandCScreen (navController: NavHostController) {
    Scaffold (
        topBar = {
            TopBar(
                canNavigateBack = true,
                showSettings = false,
                navigateUp = {navController.navigate(ScreenNames.Settings.name)}
            )
        }
    ){ innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            item {
                Text(
                    text = "Terms and Conditions",
                    style = MaterialTheme.typography.titleLarge,
                    modifier = Modifier.padding(
                        top = 20.dp,
                        start = 20.dp,
                        end = 20.dp
                    )
                )
            }
            item {
                TandCText()
            }
        }
    }
}

@Composable
fun TandCDialog(
    onDismissRequest: () -> Unit
) {
    Dialog(onDismissRequest = onDismissRequest) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight()
                .padding(10.dp),
            shape = RoundedCornerShape(15.dp),
        ) {
            LazyColumn(
                modifier = Modifier
                    .weight(1f)
            ) {
                item {
                    Text(
                        text = "Terms and Conditions",
                        style = MaterialTheme.typography.titleLarge,
                        modifier = Modifier.padding(
                            top = 20.dp,
                            start = 20.dp,
                            end = 20.dp
                        )
                    )
                }
                item {
                    TandCText()
                }
            }
            Button(
                onClick = {
                    onDismissRequest()
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(15.dp)
            ) {
                Text("OK")
            }
        }
    }
}

@Preview
@Composable
fun PreviewTandCScreen() {
    TandCScreen(navController = rememberNavController())
}

@Composable
fun TandCText(): MutableList<Unit> {
    val textModifier: Modifier = Modifier.padding(
        top = 10.dp,
        start = 25.dp,
        end = 25.dp,
        bottom = 15.dp
    )
    var textList = mutableListOf<Unit>()
    textList.add(
        Text(
            "Welcome to geoARt. By using this App, you agree to comply with and be bound by the following Terms and Conditions. Please read these Terms carefully before using the App.",
            modifier = textModifier
        )
    )
    textList.add(
        Text(
            text = "1. Acceptance of Terms",
            style = MaterialTheme.typography.titleMedium,
            modifier = textModifier
        )
    )
    textList.add(
        Text(
            "By accessing or using the App in any way, you agree to be bound by these Terms. If you do not agree with all of these Terms, please do not use the App.",
            modifier = textModifier
        )
    )
    textList.add(
        Text(
            text = "2. User Eligibility",
            style = MaterialTheme.typography.titleMedium,
            modifier = textModifier
        )
    )
    textList.add(
        Text(
            "You must be at least 18 years old to use the App. By using the App, you confirm that you are of legal age. If you are using the App on behalf of a minor, you must be the legal guardian and accept full responsibility for their use of the App.",
            modifier = textModifier
        )
    )
    textList.add(
        Text(
            "3. Privacy",
            style = MaterialTheme.typography.titleMedium,
            modifier = textModifier
        )
    )
    textList.add(
        Text(
            text = "Your use of the App is also governed by our Privacy Policy, which can be found here. Please review this policy to understand how we collect, use, and protect your personal information.",
            modifier = textModifier
        )
    )
    textList.add(
        Text(
            text = "4. User-Generated Content",
            style = MaterialTheme.typography.titleMedium,
            modifier = textModifier
        )
    )
    textList.add(
        Text(
            "a. By sharing artwork through the App, you grant geoARt a non-exclusive, worldwide, royalty-free license to use, reproduce, modify, adapt, publish, translate, distribute, and display your artwork for the purpose of promoting and improving the App.\n" +
                "b. You are solely responsible for the content you upload, including artwork, descriptions, and comments. You agree not to upload content that violates the rights of others, is offensive, illegal, or violates these Terms.\n" +
                "c. geoARt reserves the right to remove or modify any user-generated content that violates these Terms or is otherwise inappropriate.",
            modifier = textModifier
        )
    )
    textList.add(
        Text(
            text = "5. Location Services",
            style = MaterialTheme.typography.titleMedium,
            modifier = textModifier
        )
    )
    textList.add(
        Text(
            "a. The App uses location-based services to share artwork based on your location. You can enable or disable location services in your device settings.\n" +
                "b. geoARt does not guarantee the accuracy of location data and is not responsible for any consequences arising from inaccurate location information.",
            modifier = textModifier
        )
    )
    textList.add(
        Text(
            text = "6. Prohibited Conduct",
            style = MaterialTheme.typography.titleMedium,
            modifier = textModifier
        )
    )
    textList.add(
        Text(
            text = "You agree not to:\n" +
                "a. Use the App for any unlawful purpose or to promote illegal activities.\n" +
                "b. Harass, threaten, or harm other users.\n" +
                "c. Impersonate another person or misrepresent your identity.\n" +
                "d. Interfere with the operation of the App.\n" +
                "e. Engage in any activity that violates these Terms or applicable laws.",
            modifier = textModifier
        )
    )
    textList.add(
        Text(
            text = "7. Termination",
            style = MaterialTheme.typography.titleMedium,
            modifier = textModifier
        )
    )
    textList.add(
        Text(
            "geoARt reserves the right to terminate or suspend your access to the App at any time, without notice, for any reason, including a violation of these Terms.",
            modifier = textModifier
        )
    )
    textList.add(
        Text(
            text = "8. Changes to Terms",
            style = MaterialTheme.typography.titleMedium,
            modifier = textModifier
        )
    )
    textList.add(
        Text(
            "geoARt may update these Terms from time to time. You will be notified of any significant changes. Your continued use of the App after such changes constitutes your acceptance of the updated Terms.",
            modifier = textModifier
        )
    )
    textList.add(
        Text(
            text = "9. Disclaimer",
            style = MaterialTheme.typography.titleMedium,
            modifier = textModifier
        )
    )
    textList.add(
        Text(
            "The App is provided as is and without warranties of any kind, either express or implied. geoARt disclaims all warranties, including but not limited to, the implied warranties of merchantability, fitness for a particular purpose, and non-infringement.",
            modifier = textModifier
        )
    )
    textList.add(
        Text(
            text = "10. Contact Information",
            style = MaterialTheme.typography.titleMedium,
            modifier = textModifier
        )
    )
    textList.add(
        Text(
            text = "If you have any questions or concerns about these Terms, please contact us at admin@email.com",
            modifier = textModifier
        )
    )
    textList.add(
        Text(
            text = "By using the App, you acknowledge that you have read, understood, and agreed to these Terms and Conditions. Thank you for using geoARt.",
            modifier = textModifier
        )
    )
    return textList
}
