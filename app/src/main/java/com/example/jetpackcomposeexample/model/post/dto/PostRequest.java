package com.example.jetpackcomposeexample.model.post.dto;

import org.json.JSONException;
import org.json.JSONObject;

public class PostRequest {
    int id;
    String machine;


    public PostRequest fill(int id) {
        this.id = id;
        this.machine = "Samsung SM-N960U";
        return this;
    }

    public JSONObject serialize() {
        JSONObject object = new JSONObject();
        try {
            object.put("id", this.id);
            object.put("machine", this.machine);
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
        return object;
    }
}
