package com.example.jetpackcomposeexample.model.getUrl;

import org.json.JSONException;
import org.json.JSONObject;

public class GetUrlRequest {
    int machineID;

    public GetUrlRequest(int machineID) {
        this.machineID = machineID;
    }

    public JSONObject serialize() {
        JSONObject object = new JSONObject();
        try {
            object.put("machineId", this.machineID);
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
        return object;
    }
}
