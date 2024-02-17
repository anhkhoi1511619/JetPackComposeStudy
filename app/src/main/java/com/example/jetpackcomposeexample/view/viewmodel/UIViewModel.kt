package com.example.jetpackcomposeexample.view.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import com.example.jetpackcomposeexample.controller.bus.connection.CommServer
import com.example.jetpackcomposeexample.controller.server.AwsConnectHelper
import com.example.jetpackcomposeexample.controller.history.PostHistoryController
import com.example.jetpackcomposeexample.model.post.dto.Post
import com.example.jetpackcomposeexample.utils.TLog
import com.example.jetpackcomposeexample.utils.UrlConstants
import com.example.jetpackcomposeexample.utils.UrlConstants.UPLOAD_API_URL
import com.example.jetpackcomposeexample.utils.UrlConstants.UPLOAD_LOG_API_URL_HTTP
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class UIViewModel: ViewModel() {
    //Ui State
    private val _uiState = MutableStateFlow(UiState())
    val uiState: StateFlow<UiState> = _uiState.asStateFlow()

    var loadedIDList: List<String> = mutableListOf("1511619")
        private set
    var errorIDList: List<String> = mutableListOf()
        private set

    var isUpdated: Boolean = false

    var BusConnectStatus: String = CommServer.status.toString()
        private set
    fun update() {
        if (isUpdated) return
        loadFromDB()
    }

    private fun loadFromDB() {
        PostHistoryController.get(5) { result ->
            _uiState.update { currentState ->
                currentState.copy(historyPost = result)
            }
            isUpdated = true
            Log.d("PostViewModel","data base have $result")
        }
    }
    private fun addDB(post: Post) {
        PostHistoryController.set(post, System.currentTimeMillis())
    }

    fun load(id: String){
        if(loadedIDList.contains(id)) {//Avoid duplicate
            moveToDetail()
            TLog.d("PostViewModel","Load with duplicate id")
            return
        }
        TLog.d("PostViewModel","Load new id")
        loadNew(id)
    }
    private fun loadNew(id: String){
        loadedIDList += id
        AwsConnectHelper.getInstance().fetchContent(UrlConstants.POST_CONTENT_API_URL) { result ->
            Log.d("PostViewModel","result is $result")
            moveToDetail()
            _uiState.update { currentState ->
                currentState.copy(loadedDetailPost = result)
            }
            addDB(post = result)
            Log.d("PostViewModel","loadedDetailPost is ${_uiState.value.loadedDetailPost}")
        }
    }

    fun uploadLog() {
        TLog.d("PostViewModel","Screen ID is ${_uiState.value.screenID}")
        AwsConnectHelper.getInstance().upload(UPLOAD_API_URL) { result ->
            _uiState.update { currentState ->
                currentState.copy(
                        upLoadDone = result,
                        screenID = ScreenID.LOGIN
                    )
            }
            TLog.d("PostViewModel","Screen ID is ${_uiState.value.screenID}")
        }
    }
    fun startBusComm() {
        val future = CommServer.start()
        while (!future.isDone) {
            BusConnectStatus = "FAIL"
            TLog.d("MainActivity", "Result from concurrent: "+future.get().toString())
        }
    }
    fun moveToHome() {
        _uiState.update { currentState ->
            currentState.copy(screenID = ScreenID.HOME)
        }
        TLog.d("PostViewModel","Screen ID is ${_uiState.value.screenID}")
    }
    private fun moveToDetail() {
        _uiState.update { currentState ->
            currentState.copy(screenID = ScreenID.DETAIL_POST)
        }
        TLog.d("PostViewModel","Screen ID is ${_uiState.value.screenID}")
    }
    fun backHome() {
        _uiState.update { currentState ->
            currentState.copy(screenID = ScreenID.HOME)
        }
        isUpdated = false
        TLog.d("PostViewModel","Screen ID is ${_uiState.value.screenID}")
    }
}