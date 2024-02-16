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
            if(!isCorrectData(request)) return response.error();
            request.data.deserialize(request.getDataArr());
            Log.d(TAG, "Data: "+ request.data.toString());
            response.setCommand((byte) (request.getCommand()+1));
            response.setSequenceNum(request.getSequenceNum());
            response.start();
            return response.serialize();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return new byte[]{0};
    }
    boolean isCorrectData(BusDataRequest request) {
        if(request.getStx() != 0x02) {
            Log.d(TAG, "Can not parse due to STX incorrectly");
            return false;
        }
        if(request.getEtx() != 0x03) {
            Log.d(TAG, "Can not parse due to ETX incorrectly");
            return false;
        }
        byte[] dataBuffer = request.getDataArr();
        byte[] cmdSeqDataBuffer = new byte[dataBuffer.length+2];
        cmdSeqDataBuffer[0] = request.getCommand();
        cmdSeqDataBuffer[1] = (byte) request.getSequenceNum();
        System.arraycopy(dataBuffer, 0, cmdSeqDataBuffer, 2, dataBuffer.length);
        Log.d(TAG, "cmdSeqDataBuffer is prepared: "+Arrays.toString(cmdSeqDataBuffer));
        Log.d(TAG, "Size of cmdSeqDataBuffer: "+cmdSeqDataBuffer.length);
        byte sum = DataTypeConverter.sum(cmdSeqDataBuffer);
        Log.d(TAG, "Sum by user: "+sum);
        Log.d(TAG, "Sum is received: "+request.getDataSum());
        if(Math.abs(request.getDataSize()) != Math.abs(request.getDataSizeSum())){
            Log.d(TAG, "Can not parse due to DataSize is not equal with Data Size Sum");
            return false;
        }
        if((cmdSeqDataBuffer.length != Math.abs(request.getDataSize()))) {
            Log.d(TAG, "Data Size is not equal with size of data size");
            return false;
        }
        if(Math.abs(sum) != Math.abs(request.getDataSum())) {
            Log.d(TAG, "Can not parse due to Data Size incorrectly");
            return false;
        }
        return true;
    }
}
