package com.example.jetpackcomposeexample.model.bus.dto;

import android.util.Log;

import com.example.jetpackcomposeexample.utils.DataTypeConverter;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Arrays;

public class BusDataResponse {
    static final String TAG = BusDataResponse.class.getSimpleName();
    byte stx =  0x02;
    short dataSize;
    byte dataSizeSum;
    byte command = 0x00;
    int sequenceNum = 1; // 1 byte
    byte[] data = new byte[]{0};
    byte dataSum;
    byte etx = 0x03;

    public void setSequenceNum(int sequenceNum) {
        this.sequenceNum = sequenceNum;
    }

    public void setData(byte[] data) {
        this.data = data;
    }

    public void setCommand(byte command) {
        this.command = command;
    }
    public byte[] error() {
        return new byte[]{0};
    }

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

    public byte[] getData() {
        return data;
    }

    public byte[] serialize() throws IOException {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        byte[] dataBuffer = data;
        byte[] cmdSeqDataBuffer = new byte[dataBuffer.length+2];
        cmdSeqDataBuffer[0] = command;
        cmdSeqDataBuffer[1] = (byte) sequenceNum;
        System.arraycopy(dataBuffer, 0, cmdSeqDataBuffer, 2, dataBuffer.length);
        dataSize = (short) (cmdSeqDataBuffer.length);
        dataSizeSum = DataTypeConverter.sum(DataTypeConverter.toBytes(dataSize,2));
        dataSum = DataTypeConverter.sum(cmdSeqDataBuffer);
        stream.write(stx);
        stream.write(DataTypeConverter.toBytes(dataSize,2));
        stream.write(dataSizeSum);
        stream.write(command);
        stream.write(sequenceNum);
        stream.write(dataSum);
        stream.write(etx);

        return stream.toByteArray();
    }

    public void deserialize(byte[] data) {
        Log.d(TAG, "CommPackageDTO is ready to deserialize");
        int offset = 0;
        stx = data[0];
        Log.d(TAG, "stx: "+stx);
        offset++;
        byte[] dataSizeArr = Arrays.copyOfRange(data, offset, offset+2);
        Log.d(TAG, "dataSizeArr: "+Arrays.toString(dataSizeArr));
        dataSize = (short) DataTypeConverter.castInt(dataSizeArr);
        Log.d(TAG, "dataSize: "+dataSize);
        offset+=2;
        dataSizeSum = data[offset];
        Log.d(TAG, "dataSizeSum: "+dataSizeSum);
        offset++;
        command = data[offset];
        Log.d(TAG, "command: "+command);
        offset++;
        sequenceNum = data[offset];
        Log.d(TAG, "sequenceNum: "+sequenceNum);
        dataSum = data[data.length-2];
        Log.d(TAG, "dataSum: "+dataSum);
        etx = data[data.length-1];
        Log.d(TAG, "etx: "+etx);
    }
}
