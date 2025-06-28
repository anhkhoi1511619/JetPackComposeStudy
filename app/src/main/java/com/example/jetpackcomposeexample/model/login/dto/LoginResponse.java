package com.example.jetpackcomposeexample.model.login.dto;

import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Field;
import java.util.Calendar;

public class LoginResponse {
    static String accessToken ="";
    int expiresIn;
    static String tokenType = "Bearer ";

    public static String getAuthorization() {
        Log.d("LoginResponse","Send Authorization: "+ tokenType + " "+accessToken);
        return tokenType + " "+accessToken;
    }

    public void deserialize(JSONObject jsonObject) throws JSONException {
        accessToken = (String) jsonObject.get("access_token");
        expiresIn =  (int) jsonObject.get("expires_in");
        tokenType = (String) jsonObject.get("token_type");
    }
    public String toString() {
        StringBuilder ret = new StringBuilder();
        for (Field field : this.getClass().getDeclaredFields()) {
            try {
                Object val = field.get(this);
                String valStr = val != null ? val.toString() : "null";
                if(field.getType() == Calendar.class) valStr = "...";
                ret.append(field.getName()).append(": ").append(valStr).append(",");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return ret.toString();
    }


}
