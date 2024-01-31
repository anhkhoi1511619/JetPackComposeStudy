package com.example.jetpackcomposeexample.view.vico.viewModel

import androidx.lifecycle.ViewModel
import com.example.jetpackcomposeexample.aws.helper.AwsConnectHelper
import com.example.jetpackcomposeexample.controller.PostHistoryController
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

    fun update() {
        loadFromDB()
    }

    private fun loadFromDB() {
        PostHistoryController.get(5) { result ->
            println("result is $result")
            _uiState.update { currentState ->
                currentState.copy(historyPost = result)
            }
        }
    }

    fun load(id: String){
        loadedIDList += id
        AwsConnectHelper.connect(UrlConstants.POST_CONTENT_API_URL) { result ->
            println("result is $result")
            moveToDetail()
            _uiState.update { currentState ->
                currentState.copy(loadedDetailPost = result)
            }
            println("loadedDetailPost is ${_uiState.value.loadedDetailPost}")
        }
    }
    fun moveToDetail() {
        _uiState.update { currentState ->
            currentState.copy(screenID = ScreenID.DETAIL_POST)
        }
        println("isClicking is ${_uiState.value.screenID}")
    }
    fun backHome() {
        _uiState.update { currentState ->
            currentState.copy(screenID = ScreenID.HOME)
        }
        println("isClicking is ${_uiState.value.screenID}")
    }
}