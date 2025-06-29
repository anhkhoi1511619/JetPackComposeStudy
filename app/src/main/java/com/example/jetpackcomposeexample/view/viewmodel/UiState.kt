package com.example.jetpackcomposeexample.view.viewmodel

import com.example.jetpackcomposeexample.model.balance.BalanceResponse
import com.example.jetpackcomposeexample.model.card.TransitHistory
import com.example.jetpackcomposeexample.model.card.transitHistoryList
import com.example.jetpackcomposeexample.model.experience.Experiences
import com.example.jetpackcomposeexample.model.experience.experiencesExampleList
import com.example.jetpackcomposeexample.model.post.dto.Post
import com.example.jetpackcomposeexample.model.post.post1
import com.example.jetpackcomposeexample.model.post.post2
import com.example.jetpackcomposeexample.model.post.post3
import com.example.jetpackcomposeexample.model.history.HistoryDataModel
import com.example.jetpackcomposeexample.model.history.PostHistoryData
import com.example.jetpackcomposeexample.model.login.Credentials
import com.example.jetpackcomposeexample.model.login.credentialsExample

data class UiState (
    val credentials: Credentials = credentialsExample,
    val loadedDetailPost: Post = post3,
    val showingPostList: List<Experiences> = experiencesExampleList,
    val historyPost: List<PostHistoryData> = HistoryDataModel.list,
    val historyTransitList: List<TransitHistory> = transitHistoryList,
    val balanceList: ArrayList<BalanceResponse.SFInfo> = ArrayList(),
    val upLoadDone: Boolean = false,
    val screenID: ScreenID = ScreenID.FLASH
)

enum class ScreenID {
    FLASH,
    LOGIN,
    HOME,
    DETAIL_POST,
    SETTING
}