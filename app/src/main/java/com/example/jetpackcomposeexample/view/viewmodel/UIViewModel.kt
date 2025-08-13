package com.example.jetpackcomposeexample.view.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import com.example.jetpackcomposeexample.controller.server.AwsConnectHelper
import com.example.jetpackcomposeexample.controller.history.PostHistoryController
import com.example.jetpackcomposeexample.controller.tcp_ip_v2.DataProcessor
import com.example.jetpackcomposeexample.model.card.TransitHistory
import com.example.jetpackcomposeexample.model.login.Credentials
import com.example.jetpackcomposeexample.model.post.dto.Post
import com.example.jetpackcomposeexample.utils.TLog_Sync
import com.example.jetpackcomposeexample.utils.TarGzMaker
import com.example.jetpackcomposeexample.utils.UrlConstants.ADD_BALANCE_API_URL_HTTPS
import com.example.jetpackcomposeexample.utils.UrlConstants.SUBTRACT_BALANCE_URL
import com.example.jetpackcomposeexample.utils.UrlConstants.DETAIL_PROFILE_API_URL
import com.example.jetpackcomposeexample.utils.UrlConstants.LOGIN_API_URL
import com.example.jetpackcomposeexample.utils.UrlConstants.UPLOAD_API_URL
import com.example.jetpackcomposeexample.view.utils.validateValues
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class UIViewModel: ViewModel() {
    val TAG: String = "UIViewModel"
    //Ui State
    private val _uiState = MutableStateFlow(UiState())
    val uiState: StateFlow<UiState> = _uiState.asStateFlow()

    var loadedIDList: List<Int> = mutableListOf(0)
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

    fun loadTransitList(list: List<TransitHistory>) {
            _uiState.update { currentState ->
                currentState.copy(historyTransitList = list)
            }
    }
    private fun addDB(post: Post) {
        PostHistoryController.set(post, System.currentTimeMillis())
    }

    fun load(id: Int){
//        if(loadedIDList.contains(id)) {//Avoid duplicate
//            moveToDetail()
//            TLog.d(TAG,"Load with duplicate id")
//            return
//        }
        TLog_Sync.d(TAG,"Load new id")
        loadDetailProfile(id)
    }
    private fun loadDetailProfile(id: Int){
        loadedIDList += id
        AwsConnectHelper.getInstance().fetchDetailProfile(id, DETAIL_PROFILE_API_URL) { result ->
            Log.d(TAG,"result is $result")
            moveToDetail()
            _uiState.update { currentState ->
                currentState.copy(loadedDetailPost = result)
            }
//            addDB(post = result)
            Log.d(TAG,"loadedDetailPost is ${_uiState.value.loadedDetailPost}")
        }
    }

    fun uploadLog() {
        Log.d(TAG, "Make TarGz file")
        TarGzMaker.createTarGzFromCsv("/sdcard/DCIM/ProfileApp/app_log.csv", "/sdcard/DCIM/ProfileApp/app_log.tar.gz")
        Log.d(TAG, "Sending.... TarGz file")
        AwsConnectHelper.getInstance().uploadLogOkHttp(UPLOAD_API_URL, "/sdcard/DCIM/ProfileApp/app_log.tar.gz") { result ->
            if (result) TarGzMaker.delete("/sdcard/DCIM/ProfileApp/app_log.csv")
            Log.d(TAG,"result is $result")
        }
    }
    fun openSocket() {
//        SocketControllerManager.getInstance().run();
        //TODO: Socket Comm is OK then next step
        _uiState.update { currentState ->
            currentState.copy(
                screenID = ScreenID.LOGIN
            )
        }
        TLog_Sync.d(TAG,"Screen ID is ${_uiState.value.screenID}")
    }
    fun loginUI() {
        _uiState.update { currentState ->
            currentState.copy(
                screenID = ScreenID.LOGIN
            )
        }
        TLog_Sync.d(TAG,"Screen ID is ${_uiState.value.screenID}")
    }
    fun login() {
        //if(!SocketControllerManager.getInstance().isMainController) return
        AwsConnectHelper.getInstance().login(LOGIN_API_URL, {result ->
            TLog_Sync.d(TAG,"Result: " + result)
            if (result) {
                runTCP()
                moveToHome()
            }
            updateLogin(result)
            TLog_Sync.d(TAG,"Success is ${_uiState.value.credentials.isSuccessLogin}")
            TLog_Sync.d(TAG,"Screen ID is ${_uiState.value.screenID}")
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
        }
    }
    fun updateLogin(result: Boolean) {
        _uiState.update { currentState ->
            currentState.copy(
                credentials = Credentials(
                    login = currentState.credentials.login,
                    password = currentState.credentials.password,
                    remember = currentState.credentials.remember,
                    isSuccessLogin = result
                )
            )
        }
    }
    fun moveToHome() {
        _uiState.update { currentState ->
            currentState.copy(screenID = ScreenID.HOME)
        }
        changeBalance("0", "July 08", "22:40:00","reset")
        uploadLog()
        TLog_Sync.d(TAG,"Screen ID is ${_uiState.value.screenID}")
    }

    fun subtractBalance(subtractAmount: String, date: String, time:String){
        if (!validateValues(subtractAmount, date, time)) {
            TLog_Sync.d(TAG,"invalid value")
            return
        }
        changeBalance(subtractAmount, date, time, "subtract")
    }
    fun addBalance(subtractAmount: String, date: String, time:String){
        if (!validateValues(subtractAmount, date, time)) {
            TLog_Sync.d(TAG,"invalid value")
            return
        }
        changeBalance(subtractAmount, date, time, "add")
    }

    fun changeBalance(subtractAmount: String, date: String, time:String, mode:String) {
        if (mode == "add"){
            AwsConnectHelper.getInstance().addBalance(subtractAmount, date, time, mode, ADD_BALANCE_API_URL_HTTPS) { result ->
                for (s in result.sfInfos) TLog_Sync.d(TAG,"result is ${s.postAddBalance}")
                //Log.d(TAG,"result is ${result.sfInfos}")
            }
            return
        }
        AwsConnectHelper.getInstance().subtractBalance(subtractAmount, date, time, mode, SUBTRACT_BALANCE_URL) { result ->
            for (s in result.sfInfos) Log.d(TAG,"result is ${s.postSubtractBalance}")
            //Log.d(TAG,"result is ${result.sfInfos}")
            _uiState.update { currentState ->
                currentState.copy(balanceList = result.sfInfos)
            }
        }
    }
    private fun moveToDetail() {
        _uiState.update { currentState ->
            currentState.copy(screenID = ScreenID.DETAIL_POST)
        }
        TLog_Sync.d(TAG,"Screen ID is ${_uiState.value.screenID}")
    }
    fun backHome() {
        _uiState.update { currentState ->
            currentState.copy(screenID = ScreenID.HOME)
        }
        isUpdated = false
        TLog_Sync.d(TAG,"Screen ID is ${_uiState.value.screenID}")
    }

    fun runTCP() {
        DataProcessor.start()
    }
    fun changeCMDTCP(cmd: String, data: String) {
        DataProcessor.setCommand(cmd, data)
    }
}