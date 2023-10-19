/**
 * Composable components for text fields.
 */
package com.goofygoobers.geoart.ui.components

import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation

/**
 * Composable function for a password text field.
 * The password is hidden by default, with an icon on the right to toggle visibility.
 *
 * @param value The value of the text field.
 * @param onValueChange The callback to run when the value of the text field changes.
 * @param modifier The modifier to apply to the text field.
 * @param label The label of the text field.
 *
 * @see [TextField]
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PasswordField(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    label: String = "Password",
) {
    var isPasswordVisible by remember { mutableStateOf(false) }

    TextField(
        modifier = modifier,
        value = value,
        onValueChange = onValueChange,
        label = { Text(label) },
        visualTransformation = if (isPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
        trailingIcon = {
            IconButton(
                onClick = { isPasswordVisible = !isPasswordVisible },
            ) {
                val icon =
                    if (isPasswordVisible) Icons.Filled.Visibility else Icons.Filled.VisibilityOff
                Icon(imageVector = icon, contentDescription = null)
            }
        },
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
        singleLine = true,
    )
}

/**
 * Composable function for an email text field.
 *
 * @param value The value of the text field.
 * @param onValueChange The callback to run when the value of the text field changes.
 * @param modifier The modifier to apply to the text field.
 *
 * @see [TextField]
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EmailField(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    TextField(
        modifier = modifier,
        value = value,
        onValueChange = {
            onValueChange(it.trim())
        },
        label = { Text("Email") },
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
        singleLine = true,
    )
}

/**
 * Composable function for a name text field.
 *
 * @param value The value of the text field.
 * @param onValueChange The callback to run when the value of the text field changes.
 * @param modifier The modifier to apply to the text field.
 * @param label The label of the text field.
 *
 * @see [TextField]
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NameField(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    label: String = "Username",
) {
    TextField(
        label = { Text(label) },
        modifier = modifier,
        value = value,
        onValueChange = onValueChange,
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
        singleLine = true,
    )
}

/**
 * Composable function for a bio text field.
 *
 * @param value The value of the text field.
 * @param onValueChange The callback to run when the value of the text field changes.
 * @param modifier The modifier to apply to the text field.
 * @param label The label of the text field.
 *
 * @see [TextField]
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BioField(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    label: String = "Bio",
) {
    TextField(
        label = { Text(label) },
        modifier = modifier,
        value = value,
        onValueChange = onValueChange,
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
        singleLine = false,
    )
}
