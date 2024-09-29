package com.example.jetpackcomposeexample.model.aplver;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class AppVersionResponse {
    public class Version {
        public String id;
        public String version;
        public String url;
    }

    String resultCode;
    String detailResultCode;
    public ArrayList<Version> appVersion = new ArrayList<Version>();

    public void deserialize(JSONObject jsonObject) throws JSONException {
        resultCode = (String) jsonObject.get("resultCode");
        detailResultCode =  (String) jsonObject.get("resultDetailCode");
        appVersion.clear();
        if(jsonObject.isNull("outputApplicationVerList")) {
            return;
        }
        JSONArray versions = jsonObject.getJSONArray("outputApplicationVerList");
        for (int i = 0; i < versions.length(); i++) {
            Version v = new Version();
            JSONObject obj = versions.getJSONObject(i);
            v.id = obj.getString("id");
            v.version = obj.getString("version");
            v.url = obj.isNull("url")?null:obj.getString("url");
            appVersion.add(v);
        }
    }
}
