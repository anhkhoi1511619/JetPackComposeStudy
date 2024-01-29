package com.example.jetpackcomposeexample.view.vico.viewModel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.example.jetpackcomposeexample.aws.helper.AwsConnectHelper
import com.example.jetpackcomposeexample.controller.AwsDataController
import com.example.jetpackcomposeexample.model.helper.AwsDataModel
import com.example.jetpackcomposeexample.model.helper.dto.impl.post3
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

//    fun load(id: String) {
//        if (id.equals(1511619)) return
//        loadedIDList += id
//        AwsConnectHelper.connect(AwsDataController.POST_CONTENT_API_URL) { result ->
//            _uiState.update { currentState -> {
//                              currentState.loadedDetailPost = AwsDataModel.parsePostContent(result)
//                                }
//                            }
//                        }
//    }
}