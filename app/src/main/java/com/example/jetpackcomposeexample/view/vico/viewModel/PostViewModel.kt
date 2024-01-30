package com.example.jetpackcomposeexample.view.vico.viewModel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.example.jetpackcomposeexample.aws.helper.AwsConnectHelper
import com.example.jetpackcomposeexample.controller.AwsDataController
import com.example.jetpackcomposeexample.model.helper.AwsDataModel
import com.example.jetpackcomposeexample.model.helper.dto.impl.post3
import com.example.jetpackcomposeexample.utils.UrlConstants
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext

class PostViewModel: ViewModel() {
    //Ui State
    private val _uiState = MutableStateFlow(PostUiState())
    val uiState: StateFlow<PostUiState> = _uiState.asStateFlow()

    var loadedIDList: List<String> = mutableListOf("1511619")
        private set
    var errorIDList: List<String> = mutableListOf()
        private set

    suspend fun load(id: String) = withContext(Dispatchers.IO){
//        moveToDetail()
//        if (loadedIDList.contains(id)) return
        loadedIDList += id
        AwsConnectHelper.connect(UrlConstants.POST_CONTENT_API_URL) { result ->
            println("result is $result")
            _uiState.update { currentState ->
                currentState.copy(
                    isClicking = true,
                    loadedDetailPost = AwsDataModel.parsePostContent(result))
                            }
            println("isClicking is ${_uiState.value.isClicking}")
            println("loadedDetailPost is ${_uiState.value.loadedDetailPost}")
                        }
    }
    private fun moveToDetail() {
        _uiState.update { currentState ->
            currentState.copy(isClicking = true)
        }
    }
    fun backHome() {
        _uiState.update { currentState ->
            currentState.copy(isClicking = false)
        }
    }
}