package com.example.jetpackcomposeexample.model.train.dto;

import android.util.Log;

import com.example.jetpackcomposeexample.model.train.factory.Send;
import com.example.jetpackcomposeexample.utils.TrainDataUtils;

import java.io.ByteArrayOutputStream;
import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.Calendar;

public class SubControllerResponse extends TrainCommPackageDTO implements Send {
    final String TAG = "StatusDataResponse";
    public int status;
    public int currentRouteId;
    public int currentStopSeq;
    public int errorCode;

    // Here for Junit Test
    public int getStatus() {
        return status;
    }

    public int getCurrentRouteId() {
        return currentRouteId;
    }

    public int getCurrentStopSeq() {
        return currentStopSeq;
    }

    public int getErrorCode() {
        return errorCode;
    }
    // End for Junit Test

    @Override
    public byte[] serialize() {
        try {
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            stream.write(TrainDataUtils.toBytes(status,1));
            stream.write(TrainDataUtils.toBytes(currentRouteId,8));
            stream.write(TrainDataUtils.toBytes(currentStopSeq, 1));
            stream.write(TrainDataUtils.toBytes(errorCode, 6));
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
        Log.d(TAG, "Status Data Ready to deserialize");
        int offset = 0;
        status = data[offset];
        Log.d(TAG, "status: "+status);
        offset++;
        byte[] routeIdArr = Arrays.copyOfRange(data, offset, offset+8);
        currentRouteId = TrainDataUtils.castInt(routeIdArr, 0,8);//TODO: 形式 ASC. Maybe this is a bug. Fix here
        Log.d(TAG, "routeIdArr: "+Arrays.toString(routeIdArr)+"     currentRouteId: "+currentRouteId);
        offset+=8;
        currentStopSeq = (data[offset] & 0xFF);//TODO: 形式 HEX. Maybe this is a bug. Fix here
        Log.d(TAG, "stopSeqArr: "+currentStopSeq);
        offset++;
        byte[] ErrorCodeArr = Arrays.copyOfRange(data, offset, offset+6);
        errorCode = TrainDataUtils.castInt(ErrorCodeArr, 0,6);//TODO:  Maybe this is a bug. Fix here
        Log.d(TAG, "mainErrorCodeArr: "+Arrays.toString(ErrorCodeArr)+"     errorCode: "+errorCode);
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
}
