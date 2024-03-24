package com.example.jetpackcomposeexample.model.train.dto;

import android.util.Log;

import com.example.jetpackcomposeexample.model.train.factory.Send;
import com.example.jetpackcomposeexample.utils.TrainDataUtils;

import java.io.ByteArrayOutputStream;
import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.Calendar;


public class MainControllerStatusRequest extends TrainCommPackageDTO implements Send {
    final String TAG = "StatusDataRequest";
    public boolean permissionFTP;// 1byte HEX
    public boolean status;// 1byte HEX
    public int currentRouteId; // 8 bytes ASC
    public int currentStopSeq;// 1 byte HEX
    public int mainErrorCode;// 6 bytes HEX
    public int sub1ErrorCode; // 6 bytes HEX
    public int sub2ErrorCode; // 6 bytes HEX
    public int sub3ErrorCode;// 6 bytes HEX
    public boolean subBoardError; // 1byte HEX

    @Override
    public byte[] serialize() {
        try {
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            stream.write(permissionFTP ? 0x01 : 0x00);
            stream.write(status ? 0x01 : 0x00);
            stream.write(TrainDataUtils.toBytes(currentRouteId, 8));
            stream.write(TrainDataUtils.toBytes(currentStopSeq, 1));
            stream.write(TrainDataUtils.toBytes(mainErrorCode,6));
            stream.write(TrainDataUtils.toBytes(sub1ErrorCode,6));
            stream.write(TrainDataUtils.toBytes(sub2ErrorCode,6));
            stream.write(TrainDataUtils.toBytes(sub3ErrorCode,6));
            stream.write(subBoardError ? 0x01 : 0x00);
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
        byte permissionFTPByte = data[offset];
        Log.d(TAG, "permissionFTPByte: "+permissionFTPByte);
        permissionFTP = permissionFTPByte == 0x01;
        Log.d(TAG, "permissionFTP: "+permissionFTP);
        offset++;
        byte statusByte = data[offset];
        Log.d(TAG, "statusByte: "+statusByte);
        status = statusByte == 0x01;
        Log.d(TAG, "status: "+status);
        offset++;
        byte[] routeIdArr = Arrays.copyOfRange(data, offset, offset+8);
        currentRouteId = TrainDataUtils.castInt(routeIdArr, 0,8);//TODO: 形式 ASC. Maybe this is a bug. Fix here
        Log.d(TAG, "routeIdArr: "+Arrays.toString(routeIdArr)+"     currentRouteId: "+currentRouteId);
        offset+=8;
        currentStopSeq = (data[offset] & 0xFF);//TODO: 形式 HEX. Maybe this is a bug. Fix here
        Log.d(TAG, "stopSeqArr: "+currentStopSeq);
        offset++;
        byte[] mainErrorCodeArr = Arrays.copyOfRange(data, offset, offset+6);
        mainErrorCode = TrainDataUtils.castInt(mainErrorCodeArr, 0,6);//TODO:  Maybe this is a bug. Fix here
        Log.d(TAG, "mainErrorCodeArr: "+Arrays.toString(mainErrorCodeArr)+"     mainErrorCode: "+mainErrorCode);
        offset+=6;
        byte[] sub1ErrorCodeArr = Arrays.copyOfRange(data, offset, offset+6);
        sub1ErrorCode = TrainDataUtils.castInt(mainErrorCodeArr, 0,6);//TODO: Maybe this is a bug. Fix here
        Log.d(TAG, "mainErrorCodeArr: "+Arrays.toString(sub1ErrorCodeArr)+"     mainErrorCode: "+sub1ErrorCode);
        offset+=6;
        byte[] sub2ErrorCodeArr = Arrays.copyOfRange(data, offset, offset+6);
        sub2ErrorCode = TrainDataUtils.castInt(mainErrorCodeArr, 0,6);//TODO: Maybe this is a bug. Fix here
        Log.d(TAG, "mainErrorCodeArr: "+Arrays.toString(sub2ErrorCodeArr)+"     mainErrorCode: "+sub2ErrorCode);
        offset+=6;
        byte[] sub3ErrorCodeArr = Arrays.copyOfRange(data, offset, offset+6);
        sub3ErrorCode = TrainDataUtils.castInt(mainErrorCodeArr, 0,6);//TODO: Maybe this is a bug. Fix here
        Log.d(TAG, "mainErrorCodeArr: "+Arrays.toString(sub3ErrorCodeArr)+"     mainErrorCode: "+sub3ErrorCode);
        offset+=6;
        byte subBoardByte = data[offset];
        Log.d(TAG, "subBoardByte: "+subBoardByte);
        subBoardError = subBoardByte == 0x01;
        Log.d(TAG, "subBoardError: "+subBoardError);
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
