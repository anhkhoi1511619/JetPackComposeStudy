package com.example.jetpackcomposeexample.model.balance;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Field;
import java.util.Calendar;

public class AddBalanceRequest {
    String addAmount;
    String date;
    String time;

    String mode;

    public AddBalanceRequest(String addAmount, String date, String time, String mode) {
        this.addAmount = addAmount;
        this.date = date;
        this.time = time;
        this.mode = mode;
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


    public JSONObject serialize() throws RuntimeException {
        JSONObject object = new JSONObject();
        JSONObject sFInfoObj = new JSONObject();
        try {
            sFInfoObj.put("addAmount", addAmount);
            sFInfoObj.put("date", date);
            sFInfoObj.put("time", time);
            //sFInfoObj.put("mode", mode);
            object.put("sFInfo", sFInfoObj);
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
        return object;
    }
}
