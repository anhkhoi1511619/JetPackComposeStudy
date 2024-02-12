package com.example.jetpackcomposeexample.controller.bus;

import com.example.jetpackcomposeexample.model.bus.dto.BusData;

public class ResultCallback {
    static BusData busData = new BusData();
    public byte[] parse(byte[] request) {
        busData.deserialize(request);
        return busData.serialize();
    }
}
