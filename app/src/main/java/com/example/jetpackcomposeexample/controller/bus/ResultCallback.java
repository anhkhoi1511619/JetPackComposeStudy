package com.example.jetpackcomposeexample.controller.bus;

import android.util.Log;

import com.example.jetpackcomposeexample.model.bus.dto.BusData;

import java.io.IOException;

public class ResultCallback {
    BusData busData = new BusData();
    public byte[] parse(byte[] request) {
        try {
            busData.deserialize(request);
            Log.d("ResultCallback", "Data: "+busData.data.toString());
            return busData.serialize();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return new byte[]{0};
    }
}
