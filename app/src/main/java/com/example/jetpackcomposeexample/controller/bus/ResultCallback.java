package com.example.jetpackcomposeexample.controller.bus;

import android.util.Log;

import com.example.jetpackcomposeexample.model.bus.dto.BusDataRequest;
import com.example.jetpackcomposeexample.model.bus.dto.BusDataResponse;
import com.example.jetpackcomposeexample.utils.DataTypeConverter;

import java.io.IOException;
import java.util.Arrays;

public class ResultCallback {
    static final String TAG = ResultCallback.class.getSimpleName();
    BusDataRequest request = new BusDataRequest();
    public byte[] parse(byte[] rawData) {
        BusDataResponse response = new BusDataResponse();
        try {
            request.deserialize(rawData);
            if(!request.shouldRun()) return response.error();
            request.run();
            response.setCommand((byte) (request.getCommand()+1));
            response.setSequenceNum(request.getSequenceNum());
            response.run();
            return response.serialize();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return new byte[]{0};
    }
}
