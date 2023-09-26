package com.example.deco3801.ui.components

import android.util.Log
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.FilterList
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.preference.PreferenceManager
import com.example.deco3801.viewmodel.ArtFilterAction
import com.example.deco3801.viewmodel.HomeViewModel

@Composable
fun ArtFilterMenu(
    viewModel: HomeViewModel = hiltViewModel()
) {
    val context = LocalContext.current

    val filterState by viewModel.filterState.collectAsState()
    val store by remember { mutableStateOf(PreferenceManager.getDefaultSharedPreferences(context)) }

    LaunchedEffect(Unit) {
        viewModel.readFilterStateFromStore(store)
        Log.d("FILTER", "READ")
    }

    var expanded by remember { mutableStateOf(false) }
    IconButton(onClick = { expanded = true }) {
        Icon(
            imageVector = Icons.Outlined.FilterList,
            contentDescription = "Filter",
            tint = Color.White,
            modifier = Modifier.size(36.dp)
        )
    }
    DropdownMenu(
        expanded = expanded,
        onDismissRequest = { expanded = false }
    ) {

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(5.dp)
        ) {
            Text(text = "Mine Only")
            Switch(
                checked = filterState.mine,
                onCheckedChange = {
                    viewModel.onFilterAction(ArtFilterAction.Mine(it), store)
                }
            )
            Text(text = "Following Only")
            Switch(
                checked = filterState.following,
                onCheckedChange = {viewModel.onFilterAction(ArtFilterAction.Following(it), store)}
            )

        }
    }
}
