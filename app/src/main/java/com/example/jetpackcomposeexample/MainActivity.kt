package com.example.jetpackcomposeexample

import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.jetpackcomposeexample.controller.history.PostHistoryController
import com.example.jetpackcomposeexample.controller.train.SocketControllerManager
import com.example.jetpackcomposeexample.utils.TLog
import com.example.jetpackcomposeexample.view.HomeScreen
import com.example.jetpackcomposeexample.view.login.LoginForm
import com.example.jetpackcomposeexample.view.theme.JetpackComposeExampleTheme
import com.example.jetpackcomposeexample.view.viewmodel.ScreenID
import com.example.jetpackcomposeexample.view.viewmodel.UIViewModel


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            JetpackComposeExampleTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    Home()
                }
            }
        }
        TLog.d("MainActivity", "App is starting")
    }

    override fun onResume() {
        super.onResume()
        PostHistoryController(
            applicationContext
        )
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            if (!packageManager.canRequestPackageInstalls()) {
                val intent = Intent(
                    Settings.ACTION_MANAGE_UNKNOWN_APP_SOURCES, Uri.parse(
                        "package:$packageName"
                    )
                )
                startActivityForResult(intent, 1234) // Handle the result in onActivityResult
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        SocketControllerManager.getInstance().close()
    }

}


@Composable
fun Home(uiViewModel: UIViewModel =  viewModel()) {
    val postUiState by uiViewModel.uiState.collectAsState()
    when(postUiState.screenID) {
        ScreenID.FLASH -> {
            Column (
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(text = "Flash Screen")
                uiViewModel.openSocket()
            }
        }
        ScreenID.LOGIN -> {
            LoginForm(uiViewModel)
        }
        else -> {
            HomeScreen(uiViewModel)
        }
    }
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    JetpackComposeExampleTheme {
        Home()
    }
}