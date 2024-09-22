package com.example.jetpackcomposeexample.model.login

data class Credentials(
    var login: String,
    var password: String,
    var remember: Boolean = false,
    var isSuccessLogin: Boolean = true
) {
    fun isNotEmpty(): Boolean {
        return login.isEmpty() && password.isEmpty()
    }

}