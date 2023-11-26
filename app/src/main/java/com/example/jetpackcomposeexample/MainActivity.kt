package com.example.jetpackcomposeexample

import android.os.Bundle
import android.service.autofill.OnClickAction
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.jetpackcomposeexample.controller.AwsDataController
import com.example.jetpackcomposeexample.controller.AwsDataController.AWS_POST_API
import com.example.jetpackcomposeexample.model.helper.AwsConnectHelper
import com.example.jetpackcomposeexample.model.helper.dto.impl.post3
import com.example.jetpackcomposeexample.model.helper.dto.impl.postTest
import com.example.jetpackcomposeexample.view.vico.article.ArticleScreen
import com.example.jetpackcomposeexample.view.vico.theme.JetpackComposeExampleTheme
import kotlinx.coroutines.delay
import com.example.jetpackcomposeexample.view.vico.article.ArticleScreen as ArticleScreen1

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
                    Greeting("Connect Aws")
                }
            }
        }
        AwsDataController.startListener()
    }

    override fun onDestroy() {
        super.onDestroy()
        AwsConnectHelper.disConnect();
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Greeting(name: String) {
    var showButton by rememberSaveable { mutableStateOf(true) }

    if (showButton) {
        TextButton(onClick = {
            showButton = false
        }){
            AwsDataController.sendMessage(1)
            Text(text = name)
        }
    } else if (postTest == null){
        showButton = true
    }
    else {
        ArticleScreen(
            post = postTest,
            isExpandedScreen = false,
            onBack = { showButton = true },
            isFavorite = false,
            onToggleFavorite = { /*TODO*/ })
    }
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    JetpackComposeExampleTheme {
        Greeting("Connect Aws")
    }
}