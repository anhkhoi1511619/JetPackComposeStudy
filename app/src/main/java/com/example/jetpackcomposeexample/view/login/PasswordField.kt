package com.example.jetpackcomposeexample.view.login

import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Key
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import com.example.jetpackcomposeexample.view.theme.JetpackComposeExampleTheme

@Composable
fun PasswordField(
    value: String,
    onChange: (String) -> Unit,
    submit: () -> Unit,
    modifier: Modifier,
    label: String = "Password",
    placeHolder: String = "Enter your password"
) {
    var isPasswordVisible by remember { mutableStateOf(true) }
    val leadingIcon = @Composable{
        Icon(
            Icons.Default.Key,
            contentDescription = "",
            tint = MaterialTheme.colorScheme.primary
        )
    }

    val trailingIcon = @Composable {
        IconButton(onClick = { isPasswordVisible = !isPasswordVisible }) {
            Icon(
                if (isPasswordVisible) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                contentDescription = "",
                tint = MaterialTheme.colorScheme.primary
            )
        }
    }

    TextField(
        value = value,
        onValueChange = onChange,
        leadingIcon = leadingIcon,
        trailingIcon = trailingIcon,
        modifier = modifier,
        keyboardOptions = KeyboardOptions(
            imeAction = ImeAction.Done,
            keyboardType = KeyboardType.Password
            ),
        keyboardActions = KeyboardActions(
            onDone = {submit()}
        ),
        placeholder = { Text(placeHolder)},
        label = { Text(label)},
        singleLine = true,
        visualTransformation = if (isPasswordVisible) VisualTransformation.None else PasswordVisualTransformation()
        )
}

@Preview
@Composable
fun PasswordFieldTest() {
    JetpackComposeExampleTheme {
        PasswordField(value = "12345", onChange = {}, submit = { /*TODO*/ }, modifier = Modifier)
    }
}
@Preview
@Composable
fun PasswordFieldDarkThemeTest() {
    JetpackComposeExampleTheme (darkTheme = true) {
        PasswordField(value = "12345", onChange = {}, submit = { /*TODO*/ }, modifier = Modifier)
    }
}