@file:OptIn(ExperimentalComposeUiApi::class)

package com.example.jetpackcomposeexample.view.vico.article

import android.widget.Toast
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.runtime.Composable
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@Composable
fun Search(
    modifier: Modifier,
    onSearchInputChanged: (String) -> Unit
) {
    val context = LocalContext.current
    val focusManager = LocalFocusManager.current
    val keyboardController = LocalSoftwareKeyboardController.current
    
    OutlinedTextField(
        value = "",
        onValueChange = onSearchInputChanged,
        leadingIcon = { Icon(Icons.Filled.Search , contentDescription = null)},
        modifier = modifier
            .fillMaxWidth()
            .padding(top = 16.dp, bottom = 16.dp)
        ,
        singleLine = true,
        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
        keyboardActions = KeyboardActions (
            onSearch = {
                Toast.makeText(
                    context,
                    "Search is not yet implemented",
                    Toast.LENGTH_SHORT
                ).show()
                keyboardController?.hide()
            }
        )
    )

    
}

@Preview
@Composable
fun SearchTest() {
    Search(modifier = Modifier, onSearchInputChanged = {})
}