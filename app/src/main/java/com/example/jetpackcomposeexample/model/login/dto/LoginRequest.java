package com.example.jetpackcomposeexample.model.login.dto;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Field;
import java.util.Calendar;

public class LoginRequest {
    String id;
    String password;
    public LoginRequest fill(String id, String password) {
        this.id = id;
        this.password = password;
        return this;
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

    public JSONObject serialize() {
        JSONObject object = new JSONObject();
        try {
            object.put("id", this.id);
            object.put("password", this.password);
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
        return object;
    }
}
