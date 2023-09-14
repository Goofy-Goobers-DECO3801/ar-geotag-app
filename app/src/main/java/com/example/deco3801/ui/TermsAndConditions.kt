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
import com.example.deco3801.R
import com.example.deco3801.ui.components.TopBackBar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TandCScreen (
    navigateUp: () -> Unit
) {
    Scaffold (
        topBar = { TopBackBar(navigateUp) }
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
                Text(
                    text = stringResource(id = R.string.placeholder),
                    modifier = Modifier.padding(
                        top = 10.dp,
                        start = 25.dp,
                        end = 25.dp,
                        bottom = 30.dp
                    )
                )
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
                    Text(
                        text = stringResource(id = R.string.placeholder),
                        modifier = Modifier.padding(
                            top = 10.dp,
                            start = 25.dp,
                            end = 25.dp,
                            bottom = 30.dp
                        )
                    )
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
    TandCScreen({})
}