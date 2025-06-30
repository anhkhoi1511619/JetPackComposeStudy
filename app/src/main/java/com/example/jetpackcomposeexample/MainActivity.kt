package com.example.jetpackcomposeexample

import android.app.PendingIntent
import android.content.Intent
import android.content.IntentFilter
import android.nfc.NfcAdapter
import android.nfc.Tag
import android.nfc.tech.NfcF
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
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
import com.example.jetpackcomposeexample.utils.TLog
import com.example.jetpackcomposeexample.utils.TLog_Sync
import com.example.jetpackcomposeexample.utils.readTransitHistory
import com.example.jetpackcomposeexample.view.HomeScreen
import com.example.jetpackcomposeexample.view.login.LoginForm
import com.example.jetpackcomposeexample.view.theme.JetpackComposeExampleTheme
import com.example.jetpackcomposeexample.view.viewmodel.UIViewModel
import com.example.jetpackcomposeexample.view.viewmodel.ScreenID

class MainActivity : ComponentActivity() {
    private lateinit var nfcAdapter: NfcAdapter
    // 🔸 Initialize ViewModel here — use Activity's lifecycle
    private val uiViewModel: UIViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            JetpackComposeExampleTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    Home(uiViewModel)
                }
            }
        }
        try {
            nfcAdapter = NfcAdapter.getDefaultAdapter(this)
        } catch (e: Exception)
        {
            TLog_Sync.d("NfcAdapter", "Devices that do not support NFC with error " +e.message)
        }
        //nfcAdapter = NfcAdapter.getDefaultAdapter(this)
        TLog_Sync.d("MainActivity", "App is starting")
    }

    override fun onResume() {
        super.onResume()
        PostHistoryController(
            applicationContext
        )
        val intent = Intent(this, javaClass).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP)
        val pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_MUTABLE)
        val filters = arrayOf(IntentFilter(NfcAdapter.ACTION_TECH_DISCOVERED))
        val techList = arrayOf(arrayOf(NfcF::class.java.name))
        try {
            this.nfcAdapter.enableForegroundDispatch(this, pendingIntent, filters, techList)
        } catch (e: Exception)
        {
            TLog_Sync.d("NfcAdapter", "NfcAdapter is not enable Foreground" +e.message)
        }
    }

    override fun onPause() {
        super.onPause()
        try {
            nfcAdapter.disableForegroundDispatch(this)
        } catch (e: Exception)
        {
            TLog_Sync.d("NfcAdapter", "NfcAdapter is not enable ForegroundDispatch " +e.message)
        }
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        if (intent?.action == NfcAdapter.ACTION_TECH_DISCOVERED) {
            val tag: Tag? = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG)
            tag?.let {
                val idm = it.id // 8 bytes IDm
                TLog_Sync.d("NFC", "PASMO card or similar detected")
                val idmHex = idm.joinToString("") { byte -> "%02X".format(byte) }
                TLog_Sync.d("FeliCa", "IDm (PASMO IDm): $idmHex")
                Toast.makeText(this, "Card PASMO IDm: $idmHex", Toast.LENGTH_SHORT).show()
                try {
                    val nfcF = NfcF.get(tag)
                    nfcF.connect()
                    val histories = readTransitHistory(nfcF, idm)
                    histories.forEach { TLog_Sync.dLogToFileNow("SuicaHistory", it.toString()) }
                    uiViewModel.loadTransitList(histories)
                    Toast.makeText(this, "Read ${histories.size} successful record", Toast.LENGTH_SHORT).show()

                    nfcF.close()
                } catch (e: Exception) {
                    TLog_Sync.dLogToFileNow("FeliCa", "Error when tapping "+e.message)
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        //SocketControllerManager.getInstance().close()
    }

}


@Composable
fun Home(uiViewModel: UIViewModel) {
    val postUiState by uiViewModel.uiState.collectAsState()
    when(postUiState.screenID) {
        ScreenID.FLASH -> {
            Column (
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(text = "Flash Screen")
                uiViewModel.loginUI()
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
//    JetpackComposeExampleTheme {
//        Home()
//    }
}