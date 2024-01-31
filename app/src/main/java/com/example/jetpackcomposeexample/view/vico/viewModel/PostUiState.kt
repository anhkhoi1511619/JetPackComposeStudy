package com.example.jetpackcomposeexample.view.vico.viewModel

import com.example.jetpackcomposeexample.model.helper.dto.Post
import com.example.jetpackcomposeexample.model.helper.dto.impl.post1
import com.example.jetpackcomposeexample.model.helper.dto.impl.post2
import com.example.jetpackcomposeexample.model.helper.dto.impl.post3
import com.example.jetpackcomposeexample.model.helper.history.HistoryDataModel
import com.example.jetpackcomposeexample.model.helper.history.PostHistoryData

data class PostUiState (
    val loadedDetailPost:Post = post3,
    val showingPostList: List<Post> = listOf(post1, post2) + loadedDetailPost,
    val historyPost: List<PostHistoryData> = HistoryDataModel.list,
    val screenID: ScreenID = ScreenID.HOME
)

enum class ScreenID {
    HOME,
    DETAIL_POST,
    SETTING
}