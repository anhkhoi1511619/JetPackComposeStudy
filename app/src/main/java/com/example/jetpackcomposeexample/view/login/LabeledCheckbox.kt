package com.example.jetpackcomposeexample.view.login

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.jetpackcomposeexample.view.theme.JetpackComposeExampleTheme

@Composable
fun LabeledCheckbox(
    label: String = "Remember me",
    onCheckChange: ()->Unit,
    isChecked: Boolean
) {
    Row(
        Modifier
            .clickable(onClick = onCheckChange)
            .padding(4.dp)
    ) {
        Checkbox(checked = isChecked, onCheckedChange = null)
        Spacer(Modifier.size(6.dp))
        Text(label)
    }
}

@Preview
@Composable
fun LabeledCheckboxTest() {
    JetpackComposeExampleTheme {
        LabeledCheckbox(label = "Remember me", onCheckChange = { /*TODO*/ }, isChecked = true)
    }
}
@Preview
@Composable
fun LabeledCheckboxDarkThemeTest() {
    JetpackComposeExampleTheme(darkTheme = true) {
        LabeledCheckbox(label = "Remember me", onCheckChange = { /*TODO*/ }, isChecked = true)
    }
}