package com.example.jetpackcomposeexample.view.viewmodel

import com.example.jetpackcomposeexample.model.post.dto.Post
import com.example.jetpackcomposeexample.model.post.post1
import com.example.jetpackcomposeexample.model.post.post2
import com.example.jetpackcomposeexample.model.post.post3
import com.example.jetpackcomposeexample.model.history.HistoryDataModel
import com.example.jetpackcomposeexample.model.history.PostHistoryData

data class PostUiState (
    val loadedDetailPost: Post = post3,
    val showingPostList: List<Post> = listOf(post1, post2) + loadedDetailPost,
    val historyPost: List<PostHistoryData> = HistoryDataModel.list,
    val screenID: ScreenID = ScreenID.HOME
)

enum class ScreenID {
    HOME,
    DETAIL_POST,
    SETTING
}