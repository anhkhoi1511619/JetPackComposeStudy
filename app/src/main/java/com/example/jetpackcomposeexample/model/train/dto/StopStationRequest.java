package com.example.jetpackcomposeexample.model.train.dto;

import android.util.Log;

import com.example.jetpackcomposeexample.model.train.factory.Send;
import com.example.jetpackcomposeexample.utils.TrainDataUtils;

import java.io.ByteArrayOutputStream;

public class StopStationRequest extends TrainCommPackageDTO implements Send {
    final String TAG = "MainStopStationRequest";

    public int stopSeq;
    public int operationNum;

    @Override
    public byte[] serialize() {
        try {
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            stream.write(TrainDataUtils.toBytes(stopSeq,1));
            stream.write(TrainDataUtils.toBytes(operationNum,1));
            data = stream.toByteArray();
            return super.serialize();
        } catch (Exception e) {
            return new byte[]{0};
        }
    }
    @Override
    public void deserialize(byte[] data) {
        if(data == null) return;
        super.deserialize(data);
        data = super.data;
        Log.d(TAG, "MainStopStationRequest Data Ready to deserialize");
        int offset = 0;
        stopSeq = (data[offset] & 0xFF);
        Log.d(TAG, "stopSeq: "+stopSeq);
        offset++;
        operationNum = (data[offset] & 0xFF);
        Log.d(TAG, "operationNum: "+operationNum);
    }
    public static int getStopSeq(TrainCommPackageDTO dto) {
        return dto.getData()[0] & 0xFF;
    }

    public static int getOperationNum(TrainCommPackageDTO dto) {
        return dto.getData()[1] & 0xFF;
    }
}
