package com.example.jetpackcomposeexample.view.login

import android.content.res.Configuration
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.modifier.modifierLocalConsumer
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.jetpackcomposeexample.R
import com.example.jetpackcomposeexample.view.theme.JetpackComposeExampleTheme
import com.example.jetpackcomposeexample.view.viewmodel.UIViewModel

@Composable
fun LoginForm(uiViewModel: UIViewModel){
    val postUiState by uiViewModel.uiState.collectAsState()
    if (!postUiState.credentials.isSuccessLogin) { FailureLoginPopup {uiViewModel.updateLogin(true)} }
    Column (
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 30.dp)
    ){
        Text(
            text = "Welcome to Profile App",
            style = MaterialTheme.typography.titleLarge
            )
        Spacer(modifier = Modifier.size(16.dp))
        LoginField(
            value = postUiState.credentials.login,
            onChange = {data->uiViewModel.typingID(data)},
            modifier = Modifier.fillMaxWidth()
        )
        PasswordField(
            value = postUiState.credentials.password,
            onChange = {data->uiViewModel.typingPassword(data)},
            submit = {},
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(10.dp))
        LabeledCheckbox(
            onCheckChange = {uiViewModel.onCheckRemember() },
            isChecked = postUiState.credentials.remember
        )
        Button(
            onClick = { uiViewModel.login() },
            enabled = true,
            shape = RoundedCornerShape(5.dp),
            modifier = Modifier.fillMaxWidth()
            ) {
            Text(text = "Login")
        }
    }
}
@Composable
fun FailureLoginPopup(onDismiss: ()->Unit) {
    AlertDialog(onDismissRequest = onDismiss,
        text = {
            Text(text = stringResource(id = R.string.failure_login_popup),
                style = MaterialTheme.typography.bodyMedium)
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text(text = stringResource(id = R.string.close))
            }
        })
}

@Preview(device = "spec:width=1280dp,height=800dp,dpi=240", showSystemUi = true,
    showBackground = true
)
@Composable
fun LoginFormWhiteScreen() {
    JetpackComposeExampleTheme {
        LoginForm(viewModel())
    }
}

@Preview(device = "id:pixel", showSystemUi = true, showBackground = true,
    uiMode = Configuration.UI_MODE_NIGHT_YES or Configuration.UI_MODE_TYPE_NORMAL
)
@Composable
fun LoginFormBlackScreen() {
    JetpackComposeExampleTheme (darkTheme = true) {
        LoginForm(viewModel())
    }
}