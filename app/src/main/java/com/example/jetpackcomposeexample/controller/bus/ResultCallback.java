package com.example.jetpackcomposeexample.controller.bus;

import android.util.Log;

import com.example.jetpackcomposeexample.model.bus.dto.BusData;
import com.example.jetpackcomposeexample.model.bus.dto.CommPackageDTO;

import java.io.IOException;

public class ResultCallback {
    BusData busData = new BusData();
    CommPackageDTO commPackageDTO = new CommPackageDTO();
    public byte[] parse(byte[] request) {
        try {
            busData.deserialize(request);
            Log.d("ResultCallback", "Data: "+busData.data.toString());
            commPackageDTO.setCommand((byte) (busData.getCommand()+1));
            commPackageDTO.setSequenceNum(busData.getSequenceNum());
//            commPackageDTO.setData(busData);
            return commPackageDTO.serialize();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return new byte[]{0};
    }
}
