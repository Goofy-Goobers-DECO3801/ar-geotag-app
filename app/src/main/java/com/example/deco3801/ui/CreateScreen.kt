package com.example.deco3801.ui

import com.example.deco3801.ui.components.NavBar
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.example.deco3801.ui.theme.MyColors

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateScreen() {
    val textModifier : Modifier = Modifier
    val textFieldModifier : Modifier = Modifier.fillMaxWidth()
    val spacerModifier : Modifier = Modifier.height(10.dp)

    var title by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(
                start = 35.dp,
                end = 35.dp,
                top = 40.dp
            )
    ) {
        Text(
            text = "Upload an Artwork",
            modifier = textModifier,
            style = MaterialTheme.typography.titleLarge,
            color = MyColors.DarkOrange
        )
        Spacer(modifier = spacerModifier)


        Text(
            text = "Title",
            modifier = textModifier,
            style = MaterialTheme.typography.titleMedium
        )
        TextField(
            value = title,
            onValueChange = {newTitle -> title = newTitle},
            modifier = textFieldModifier
        )
        Spacer(modifier = spacerModifier)


        Text(
            text = "Upload Artwork",
            style = MaterialTheme.typography.titleMedium
        )
        Button(onClick = { /*TODO*/ }) {
            Text(text = "Upload from files")
        }
        Spacer(modifier = spacerModifier)


        Text(
            text = "Artwork Description",
            style = MaterialTheme.typography.titleMedium
        )
        TextField(
            value = description,
            onValueChange = {newDescription -> description = newDescription},
            modifier = textFieldModifier.height(130.dp)
        )
        Spacer(modifier = spacerModifier)


        Text(
            text = "Select Location",
            style = MaterialTheme.typography.titleMedium
        )
        Button(onClick = { /*TODO*/ }) {
            Text(text = "Select Location")
        }
        Spacer(modifier = spacerModifier)


        Row(modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center
        ){
            Button(onClick = { /*TODO*/ }){
                Text(text = "Post Artwork")
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun CreateScreenPreview() {
    CreateScreen()
}
