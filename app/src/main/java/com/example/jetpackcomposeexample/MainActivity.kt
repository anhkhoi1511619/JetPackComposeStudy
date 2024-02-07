package com.example.jetpackcomposeexample

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.jetpackcomposeexample.controller.PostHistoryController
import com.example.jetpackcomposeexample.controller.helper.AwsConnectHelper
import com.example.jetpackcomposeexample.utils.TLog
import com.example.jetpackcomposeexample.utils.UrlConstants.UPLOAD_LOG_API_URL_HTTP
import com.example.jetpackcomposeexample.view.article.HomeScreen
import com.example.jetpackcomposeexample.view.theme.JetpackComposeExampleTheme
import com.example.jetpackcomposeexample.view.viewmodel.PostViewModel
import com.example.jetpackcomposeexample.view.viewmodel.ScreenID

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
        PostHistoryController(applicationContext)
    }

}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Home(postViewModel: PostViewModel =  viewModel()) {
    val postUiState by postViewModel.uiState.collectAsState()
    when(postUiState.screenID) {
        ScreenID.LOGIN -> {
            TextButton(onClick = {
                postViewModel.uploadLog()
            }){
                Text(text = "Login")
            }
        }
        else -> {
            HomeScreen(postViewModel)
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