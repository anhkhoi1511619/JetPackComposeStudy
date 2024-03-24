package com.example.jetpackcomposeexample.model.train.dto;

import android.util.Log;

import com.example.jetpackcomposeexample.utils.TrainDataUtils;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.Calendar;

public class TrainCommPackageDTO {
    static final String TAG = TrainCommPackageDTO.class.getSimpleName();
    byte stx =  0x02;
    short dataSize;
    byte dataSizeSum;
    int command = 0;
    int sequenceNum = 1; // 1 byte
    byte[] data;
    byte dataSum;
    byte etx = 0x03;

    public int getCommand() {
        return command;
    }

    public int getSequenceNum() {
        return sequenceNum;
    }

    public void setSequenceNum(int sequenceNum) {
        this.sequenceNum = sequenceNum;
    }

    public void setData(byte[] data) {
        this.data = data;
    }

    public void setCommand(int command) {
        this.command = command;
    }

    public byte[] getData() {
        return data;
    }
    public boolean shouldRun() {
        return isCorrectData();
    }



    //For UnitTest
    public byte getStx() {
        return stx;
    }

    public short getDataSize() {
        return dataSize;
    }

    public byte getDataSizeSum() {
        return dataSizeSum;
    }

    public byte getDataSum() {
        return dataSum;
    }

    public byte getEtx() {
        return etx;
    }
    //End for Unit Test


    public byte[] serialize() throws IOException {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        byte[] dataBuffer = (data == null) ? new byte[]{} : data;//TODO: Maybe this is BUG
        byte[] cmdSeqDataBuffer = new byte[dataBuffer.length+2];
        cmdSeqDataBuffer[0] = (byte) command;
        cmdSeqDataBuffer[1] = (byte) sequenceNum;
        System.arraycopy(dataBuffer, 0, cmdSeqDataBuffer, 2, dataBuffer.length);
        dataSize = (short) (cmdSeqDataBuffer.length);
        dataSizeSum = TrainDataUtils.sum(TrainDataUtils.toBytes(dataSize,2));
        dataSum = TrainDataUtils.sum(cmdSeqDataBuffer);
        stream.write(stx);
        stream.write(TrainDataUtils.toBytes(dataSize,2));
        stream.write(dataSizeSum);
        stream.write(command);
        stream.write(sequenceNum);
        if(data != null) stream.write(data);
        stream.write(dataSum);
        stream.write(etx);

        return stream.toByteArray();
    }
    public void deserialize(byte[] ret) {
        if(ret.length<=0) return;
        int offset = 0;
        stx = ret[offset];
        Log.d(TAG, "stx: "+stx);
        offset++;
        byte[] dataSizeArr = Arrays.copyOfRange(ret, offset, offset+2);
        Log.d(TAG, "dataSizeArr: "+Arrays.toString(dataSizeArr));
        dataSize = (short) TrainDataUtils.castInt(dataSizeArr);
        Log.d(TAG, "dataSize: "+dataSize);
        offset+=2;
        dataSizeSum = ret[offset];
        Log.d(TAG, "dataSizeSum: "+dataSizeSum);
        offset++;
        command = (ret[offset] & 0xFF);
        Log.d(TAG, "command: "+command);
        offset++;
        sequenceNum = ret[offset];
        Log.d(TAG, "sequenceNum: "+sequenceNum);
        offset++;
        data = Arrays.copyOfRange(ret, offset, ret.length-2);
        Log.d(TAG, "dataArr: "+Arrays.toString(data));
        dataSum = ret[ret.length-2];
        Log.d(TAG, "dataSum: "+dataSum);
        etx = ret[ret.length-1];
        Log.d(TAG, "etx: "+etx);
    }

    public boolean isCorrectData() {
        if(stx != 0x02) {
            Log.e(TAG, "Can not parse due to STX incorrectly");
            return false;
        }
        if(etx != 0x03) {
            Log.e(TAG, "Can not parse due to ETX incorrectly");
            return false;
        }
        byte[] dataBuffer = (data==null) ? new byte[]{} : data;
        byte[] cmdSeqDataBuffer = new byte[dataBuffer.length+2];
        cmdSeqDataBuffer[0] = (byte) command;
        cmdSeqDataBuffer[1] = (byte) sequenceNum;
        System.arraycopy(dataBuffer, 0, cmdSeqDataBuffer, 2, dataBuffer.length);
        Log.d(TAG, "cmdSeqDataBuffer is prepared: "+Arrays.toString(cmdSeqDataBuffer));
        Log.d(TAG, "Size of cmdSeqDataBuffer: "+cmdSeqDataBuffer.length);
        byte sum = TrainDataUtils.sum(cmdSeqDataBuffer);
        Log.d(TAG, "Sum by user: "+sum);
        Log.d(TAG, "Sum is received: "+dataSum);
        if(Math.abs(dataSize) != Math.abs(dataSizeSum)){
            Log.e(TAG, "Can not parse due to DataSize is not equal with Data Size Sum");
            return false;
        }
        if((cmdSeqDataBuffer.length != Math.abs(dataSize))) {
            Log.e(TAG, "Data Size is not equal with size of data size");
            return false;
        }
        if(Math.abs(sum) != Math.abs(dataSum)) {
            Log.e(TAG, "Can not parse due to Data Size incorrectly");
            return false;
        }
        return true;
    }
    public String toString() {
        StringBuilder ret = new StringBuilder();
        for (Field field : this.getClass().getFields()) {
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
