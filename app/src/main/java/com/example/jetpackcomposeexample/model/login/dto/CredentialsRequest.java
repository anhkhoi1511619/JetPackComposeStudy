package com.example.jetpackcomposeexample.model.login.dto;

import com.example.jetpackcomposeexample.model.post.dto.PostRequest;

import org.json.JSONException;
import org.json.JSONObject;

public class CredentialsRequest {
    String id;
    String password;
    public CredentialsRequest fill(String id, String password) {
        this.id = id;
        this.password = password;
        return this;
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
