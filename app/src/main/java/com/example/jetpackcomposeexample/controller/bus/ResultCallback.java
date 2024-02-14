package com.example.jetpackcomposeexample.controller.bus;

import android.util.Log;

import com.example.jetpackcomposeexample.model.bus.dto.BusDataRequest;
import com.example.jetpackcomposeexample.model.bus.dto.BusDataResponse;
import com.example.jetpackcomposeexample.utils.DataTypeConverter;

import java.io.IOException;

public class ResultCallback {
    BusDataRequest busDataRequest = new BusDataRequest();
    public byte[] parse(byte[] request) {
        BusDataResponse busDataResponse = new BusDataResponse();
        try {
            if(!isCorrectData(busDataResponse,request)) return busDataResponse.error();
            busDataRequest.deserialize(request);
            Log.d("ResultCallback", "Data: "+ busDataRequest.data.toString());
            busDataResponse.setCommand((byte) (busDataRequest.getCommand()+1));
            busDataResponse.setSequenceNum(busDataRequest.getSequenceNum());
//            commPackageDTO.setData(busData);
            return busDataResponse.serialize();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return new byte[]{0};
    }
    boolean isCorrectData(BusDataResponse dto, byte[] request) {
        dto.deserialize(request);
        if(dto.getStx() != 0x02) return false;
        if(dto.getEtx() != 0x03) return false;
        if(dto.getDataSize() != dto.getDataSizeSum()) return false;//TODO: Maybe this is bug
        if(DataTypeConverter.sum(dto.getData()) != dto.getDataSize()) return false;
        return true;
    }
}
