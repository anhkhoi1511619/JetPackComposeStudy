package com.example.jetpackcomposeexample.model.balance;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Calendar;

public class SubtractBalanceResponse {
    public static class SFInfo {
        public SFInfo(String postSubtractBalance, String date) {
            this.postSubtractBalance = postSubtractBalance;
            this.date = date;
        }

        public SFInfo(String postSubtractBalance, String date, String time) {
            this.postSubtractBalance = postSubtractBalance;
            this.date = date;
            this.time = time;
        }

        public SFInfo() {
        }

        public String subtractAmount;
        public String postSubtractBalance;
        public String date;
        public String time;
    }
    public String resultCode = "";
    public String resultDetailCode = "";
    public ArrayList<SFInfo> sfInfos = new ArrayList<>();

    public void deserialize(JSONObject jsonObject) throws JSONException {
        resultCode = (String) jsonObject.get("resultCode");
        resultDetailCode = (String) jsonObject.get("resultDetailCode");
        sfInfos.clear();
        if(jsonObject.isNull("sFInfo")) return;
        JSONArray jsonArray = jsonObject.getJSONArray("sFInfo");
        for (int i = 0; i < jsonArray.length(); i++) {
            SFInfo sfInfo = new SFInfo();
            JSONObject object = jsonArray.getJSONObject(i);
            sfInfo.subtractAmount = object.getString("subtractAmount");
            sfInfo.postSubtractBalance = object.getString("postSubtractBalance");
            sfInfo.time = object.getString("time");
            sfInfo.date = object.getString("date");
            sfInfos.add(sfInfo);
        }
    }

    public String toString() {
        StringBuilder ret = new StringBuilder();
        for (Field field : this.getClass().getFields()) {
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
