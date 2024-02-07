package com.example.jetpackcomposeexample.view.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import com.example.jetpackcomposeexample.controller.server.AwsConnectHelper
import com.example.jetpackcomposeexample.controller.history.PostHistoryController
import com.example.jetpackcomposeexample.model.post.dto.Post
import com.example.jetpackcomposeexample.utils.TLog
import com.example.jetpackcomposeexample.utils.UrlConstants
import com.example.jetpackcomposeexample.utils.UrlConstants.UPLOAD_LOG_API_URL_HTTP
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class PostViewModel: ViewModel() {
    //Ui State
    private val _uiState = MutableStateFlow(PostUiState())
    val uiState: StateFlow<PostUiState> = _uiState.asStateFlow()

    var loadedIDList: List<String> = mutableListOf("1511619")
        private set
    var errorIDList: List<String> = mutableListOf()
        private set

    var isUpdated: Boolean = false

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
            Log.d("PostViewModel","Duplicate id")
            return
        }
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
        AwsConnectHelper.getInstance().upload(UPLOAD_LOG_API_URL_HTTP) { result ->
            _uiState.update { currentState ->
                currentState.copy(
                        upLoadDone = result,
                        screenID = ScreenID.LOGIN
                    )
            }
            TLog.d("PostViewModel","Screen ID is ${_uiState.value.screenID}")
        }
    }
    fun moveToHome() {
        _uiState.update { currentState ->
            currentState.copy(screenID = ScreenID.HOME)
        }
        TLog.d("PostViewModel","Screen ID is ${_uiState.value.screenID}")
    }
    fun moveToDetail() {
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