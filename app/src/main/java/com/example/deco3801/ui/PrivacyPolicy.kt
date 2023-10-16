package com.example.deco3801.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
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
import org.w3c.dom.Text

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PrivacyPolicyScreen (navController: NavHostController) {
    Scaffold (
        topBar = {
            TopBar(
                navController = navController,
                canNavigateBack = true,
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
                    text = "Privacy Policy",
                    style = MaterialTheme.typography.titleLarge,
                    modifier = Modifier.padding(
                        top = 20.dp,
                        start = 20.dp,
                        end = 20.dp
                    )
                )
            }
            item {
                PrivacyPolicyText()
            }
        }
    }
}

@Composable
fun PrivacyPolicyDialog(
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
                        text = "Privacy Policy",
                        style = MaterialTheme.typography.titleLarge,
                        modifier = Modifier.padding(
                            top = 20.dp,
                            start = 20.dp,
                            end = 20.dp
                        )
                    )
                }
                item {
                    PrivacyPolicyText()
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
fun PreviewPrivacyPolicyScreen() {
    TandCScreen(navController = rememberNavController())
}

@Composable
fun PrivacyPolicyText(): MutableList<Unit> {
    val textModifier : Modifier = Modifier.padding(
        top = 10.dp,
        start = 25.dp,
        end = 25.dp,
        bottom = 15.dp
    )
    var textList = mutableListOf<Unit>()
    textList.add(Text(
        "At geoARt, we are committed to protecting your privacy. This Privacy Policy explains how we collect, use, disclose, and safeguard your personal information. By using the geoARt app, you consent to the practices described in this policy.",
        modifier = textModifier
    ))
    textList.add(Text(
        text = "1. Information We Collect",
        style = MaterialTheme.typography.titleMedium,
        modifier = textModifier
    ))
    textList.add(Text(
        text = "a: User-Provided Information: When you use the App, you may provide us with information such as your name, email address, and location. You may also upload artwork and other content to share with other users.",
        modifier = textModifier
    ))
    textList.add(Text(
        text = "b: Automatically Collected Information: We may collect certain information automatically, including your device type, operating system, unique device identifiers, IP address, and usage data.",
        modifier = textModifier
    ))
    textList.add(Text(
        text = "2. How We Use Your Information",
        style = MaterialTheme.typography.titleMedium,
        modifier = textModifier
    ))
    textList.add(Text(
        text = "a. Personalization: We use your information to personalize your experience with the App, including displaying artwork and content relevant to your location.",
        modifier = textModifier
    ))
    textList.add(Text(
        text = "b. Communication: We may send you notifications, updates, and relevant information about the App. You can opt out of receiving these communications at any time.",
        modifier = textModifier
    ))
    textList.add(Text(
        text = "c. Analytics: We use data analytics to improve the functionality and performance of the App.",
        modifier = textModifier
    ))
    textList.add(Text(
        text = "3. Sharing Your Information",
        style = MaterialTheme.typography.titleMedium,
        modifier = textModifier
    ))
    textList.add(Text(
        text = "a. User-Generated Content: Your artwork and content may be shared with other users based on your location settings. Please be aware that any content you share on the App is publicly visible.",
        modifier = textModifier
    ))
    textList.add(Text(
        text = "b. Third Parties: We may share your information with third-party service providers to help us operate and improve the App. These providers are bound by confidentiality agreements and are not permitted to use your information for any other purpose.",
        modifier = textModifier
    ))
    textList.add(Text(
        text = "4. Data Security",
        style = MaterialTheme.typography.titleMedium,
        modifier = textModifier
    ))
    textList.add(Text(
        text = "We take reasonable measures to protect your personal information from unauthorized access or disclosure. However, no method of data transmission over the internet or electronic storage is 100% secure, and we cannot guarantee absolute security.",
        modifier = textModifier
    ))
    textList.add(Text(
        text = "5. Your Choices",
        style = MaterialTheme.typography.titleMedium,
        modifier = textModifier
    ))
    textList.add(Text(
        text = "You have the following choices regarding your personal information:",
        modifier = textModifier
    ))
    textList.add(Text(
        text = "a. Access and Correction: You can access and edit your profile information within the App.",
        modifier = textModifier
    ))
    textList.add(Text(
        text = "b. Deletion: You can request the deletion of your account and associated data by contacting us.",
        modifier = textModifier
    ))
    textList.add(Text(
        text = "6. Children's Privacy",
        style = MaterialTheme.typography.titleMedium,
        modifier = textModifier
    ))
    textList.add(Text(
        text = "geoARt is not intended for use by children under the age of 18. If you are a parent or guardian and believe that your child has provided us with personal information, please contact us, and we will take steps to remove that information from our servers.",
        modifier = textModifier
    ))
    textList.add(Text(
        text = "7. Changes to this Privacy Policy",
        style = MaterialTheme.typography.titleMedium,
        modifier = textModifier
    ))
    textList.add(Text(
        text = "We may update this Privacy Policy from time to time. We will notify you of any significant changes by posting the updated policy within the App.",
        modifier = textModifier
    ))
    textList.add(Text(
        text = "8. Contact Us",
        style = MaterialTheme.typography.titleMedium,
        modifier = textModifier
    ))
    textList.add(Text(
        text = "If you have any questions, concerns, or requests regarding this Privacy Policy or the use of your personal information, please contact us at admin@email.com.",
        modifier = textModifier
    ))
    return textList
}

