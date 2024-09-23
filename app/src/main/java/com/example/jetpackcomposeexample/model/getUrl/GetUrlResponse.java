package com.example.jetpackcomposeexample.model.getUrl;

import org.json.JSONException;
import org.json.JSONObject;

public class GetUrlResponse {
    String resultCode;
    String detailResultCode;
    public String url;

    public void deserialize(JSONObject jsonObject) throws JSONException {
        resultCode = (String) jsonObject.get("resultCode");
        detailResultCode =  (String) jsonObject.get("resultDetailCode");
        url = (String) jsonObject.getJSONObject("uploadUrl").get("uploadUrl");
    }
}
