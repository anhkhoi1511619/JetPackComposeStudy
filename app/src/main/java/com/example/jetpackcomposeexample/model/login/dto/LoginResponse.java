package com.example.jetpackcomposeexample.model.login.dto;

import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

public class LoginResponse {
    public String accessToken ="";
    int expiresIn;
    public String tokenType = "Bearer ";

//    public static String getAuthorization() {
//        Log.d("LoginResponse","Send Authorization: "+ tokenType + " "+accessToken);
//        return tokenType + " "+accessToken;
//    }

    public void deserialize(JSONObject jsonObject) throws JSONException {
        accessToken = (String) jsonObject.get("access_token");
        expiresIn =  (int) jsonObject.get("expires_in");
        tokenType = (String) jsonObject.get("token_type");
    }


}
