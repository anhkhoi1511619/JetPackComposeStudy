package com.example.jetpackcomposeexample.view.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import com.example.jetpackcomposeexample.controller.helper.AwsConnectHelper
import com.example.jetpackcomposeexample.controller.PostHistoryController
import com.example.jetpackcomposeexample.model.post.Post
import com.example.jetpackcomposeexample.utils.UrlConstants
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
        loadedIDList += id
        AwsConnectHelper.fetchContent(UrlConstants.POST_CONTENT_API_URL) { result ->
            Log.d("PostViewModel","result is $result")
            moveToDetail()
            _uiState.update { currentState ->
                currentState.copy(loadedDetailPost = result)
            }
            addDB(post = result)
            Log.d("PostViewModel","loadedDetailPost is ${_uiState.value.loadedDetailPost}")
        }
    }
    fun moveToDetail() {
        _uiState.update { currentState ->
            currentState.copy(screenID = ScreenID.DETAIL_POST)
        }
        Log.d("PostViewModel","isClicking is ${_uiState.value.screenID}")
    }
    fun backHome() {
        _uiState.update { currentState ->
            currentState.copy(screenID = ScreenID.HOME)
        }
        isUpdated = false
        Log.d("PostViewModel","isClicking is ${_uiState.value.screenID}")
    }
}