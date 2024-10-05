package com.example.jetpackcomposeexample.model.aplver;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class AppVersionRequest {
    public class Version {
        public String id;
        public String version;
    }
    int machineID;
    public ArrayList<Version> appVersion = new ArrayList<Version>();

    public void addVersion(String id, String version) {
        if(version.length() == 20) {
            // 12_0002_20231116_001 -> 20231116_001
            version = version.substring(8);
        } else {
            Log.w("JSON", "Invalid version notation "+version);
        }
        Version v = new Version();
        v.id = id;
        v.version = version;
        appVersion.add(v);
    }
    public AppVersionRequest(int machineID) {
        this.machineID = machineID;
    }

    public JSONObject serialize() throws JSONException {
        JSONObject object = new JSONObject();
        object.put("machineId", this.machineID);
        JSONArray versions = new JSONArray();
        for (Version v: appVersion) {
            versions.put((new JSONObject())
                    .put("id", v.id)
                    .put("version", v.version)
            );
        }
        object.put("appVerList", versions);
        return object;
    }
}
