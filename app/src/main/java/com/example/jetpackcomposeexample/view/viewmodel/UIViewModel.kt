package com.example.jetpackcomposeexample.view.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import com.example.jetpackcomposeexample.controller.server.AwsConnectHelper
import com.example.jetpackcomposeexample.controller.history.PostHistoryController
import com.example.jetpackcomposeexample.controller.train.SocketControllerManager
import com.example.jetpackcomposeexample.model.login.Credentials
import com.example.jetpackcomposeexample.model.post.dto.Post
import com.example.jetpackcomposeexample.utils.TLog
import com.example.jetpackcomposeexample.utils.UrlConstants
import com.example.jetpackcomposeexample.utils.UrlConstants.LOGIN_API_URL
import com.example.jetpackcomposeexample.utils.UrlConstants.UPLOAD_API_URL
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class UIViewModel: ViewModel() {
    val TAG: String = "UIViewModel"
    //Ui State
    private val _uiState = MutableStateFlow(UiState())
    val uiState: StateFlow<UiState> = _uiState.asStateFlow()

    var loadedIDList: List<String> = mutableListOf("1511619")
        private set
    var errorIDList: List<String> = mutableListOf()
        private set

    var isUpdated: Boolean = false

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
            Log.d(TAG,"data base have $result")
        }
    }
    private fun addDB(post: Post) {
        PostHistoryController.set(post, System.currentTimeMillis())
    }

    fun load(id: String){
        if(loadedIDList.contains(id)) {//Avoid duplicate
            moveToDetail()
            TLog.d(TAG,"Load with duplicate id")
            return
        }
        TLog.d(TAG,"Load new id")
        loadNew(id)
    }
    private fun loadNew(id: String){
        loadedIDList += id
        AwsConnectHelper.getInstance().fetchContent(UrlConstants.POST_CONTENT_API_URL) { result ->
            Log.d(TAG,"result is $result")
            moveToDetail()
            _uiState.update { currentState ->
                currentState.copy(loadedDetailPost = result)
            }
            addDB(post = result)
            Log.d(TAG,"loadedDetailPost is ${_uiState.value.loadedDetailPost}")
        }
    }

    fun uploadLog() {
        TLog.d(TAG,"Screen ID is ${_uiState.value.screenID}")
        AwsConnectHelper.getInstance().upload(UPLOAD_API_URL) { result ->
            _uiState.update { currentState ->
                currentState.copy(
                        upLoadDone = result,
                        screenID = ScreenID.LOGIN
                    )
            }
            TLog.d(TAG,"Screen ID is ${_uiState.value.screenID}")
        }
    }
    fun openSocket() {
        SocketControllerManager.getInstance().run();
        //TODO: Socket Comm is OK then next step
        _uiState.update { currentState ->
            currentState.copy(
                screenID = ScreenID.LOGIN
            )
        }
        TLog.d(TAG,"Screen ID is ${_uiState.value.screenID}")
    }
    fun login() {
        if(!SocketControllerManager.getInstance().isMainController) return
        AwsConnectHelper.getInstance().login(LOGIN_API_URL, {result ->
            if (result) moveToHome()
            TLog.d(TAG,"Screen ID is ${_uiState.value.screenID}")

        },_uiState.value.credentials)
    }
    fun typingID(ID: String) {
        _uiState.update { currentState ->
            currentState.copy(
                credentials = Credentials(
                    login = ID,
                    password = ""
                )
            )
        }
    }
    fun typingPassword(password: String) {
        _uiState.update { currentState ->
            currentState.copy(
                credentials = Credentials(
                    login = currentState.credentials.login,
                    password = password
                )
            )
        }
    }
    fun onCheckRemember() {
        _uiState.update { currentState ->
            currentState.copy(
                credentials = Credentials(
                    login = currentState.credentials.login,
                    password = currentState.credentials.password,
                    remember = !currentState.credentials.remember
                )
            )
        }    }
    fun moveToHome() {
        _uiState.update { currentState ->
            currentState.copy(screenID = ScreenID.HOME)
        }
        TLog.d(TAG,"Screen ID is ${_uiState.value.screenID}")
    }
    private fun moveToDetail() {
        _uiState.update { currentState ->
            currentState.copy(screenID = ScreenID.DETAIL_POST)
        }
        TLog.d(TAG,"Screen ID is ${_uiState.value.screenID}")
    }
    fun backHome() {
        _uiState.update { currentState ->
            currentState.copy(screenID = ScreenID.HOME)
        }
        isUpdated = false
        TLog.d(TAG,"Screen ID is ${_uiState.value.screenID}")
    }
}