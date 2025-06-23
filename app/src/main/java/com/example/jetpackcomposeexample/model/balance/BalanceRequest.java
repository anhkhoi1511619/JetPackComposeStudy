package com.example.jetpackcomposeexample.model.balance;

import org.json.JSONException;
import org.json.JSONObject;

public class BalanceRequest {
    String subtractAmount;
    String date;
    String time;

    public BalanceRequest(String subtractAmount, String date, String time) {
        this.subtractAmount = subtractAmount;
        this.date = date;
        this.time = time;
    }


    public JSONObject serialize() throws RuntimeException {
        JSONObject object = new JSONObject();
        JSONObject sFInfoObj = new JSONObject();
        try {
            sFInfoObj.put("subtractAmount", subtractAmount);
            sFInfoObj.put("date", date);
            sFInfoObj.put("time", time);
            object.put("sFInfo", sFInfoObj);
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
        return object;
    }
}
