package com.example.jetpackcomposeexample.model.bus.dto;

import android.util.Log;

import com.example.jetpackcomposeexample.utils.DataTypeConverter;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Arrays;

public abstract class CommPackageDTO {
    static final String TAG = CommPackageDTO.class.getSimpleName();
    byte stx =  0x02;
    short dataSize;
    byte dataSizeSum;
    byte command = 0x00;
    int sequenceNum = 1; // 1 byte
    byte[] data;
    byte dataSum;
    byte etx = 0x03;

    public byte getCommand() {
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

    public void setCommand(byte command) {
        this.command = command;
    }
    public byte[] error() {
        return new byte[]{0};
    }

    public byte[] getData() {
        return data;
    }
    public void run(){
        doRun();
    }
    public boolean shouldRun() {
        return isCorrectData();
    }

    protected abstract void doRun();
    public byte[] serialize() throws IOException {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        byte[] dataBuffer = (data == null) ? new byte[]{0} : data;
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
        dataSize = (short) DataTypeConverter.castInt(dataSizeArr);
        Log.d(TAG, "dataSize: "+dataSize);
        offset+=2;
        dataSizeSum = ret[offset];
        Log.d(TAG, "dataSizeSum: "+dataSizeSum);
        offset++;
        command = ret[offset];
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

    boolean isCorrectData() {
        if(stx != 0x02) {
            Log.d(TAG, "Can not parse due to STX incorrectly");
            return false;
        }
        if(etx != 0x03) {
            Log.d(TAG, "Can not parse due to ETX incorrectly");
            return false;
        }
        byte[] dataBuffer = data;
        byte[] cmdSeqDataBuffer = new byte[dataBuffer.length+2];
        cmdSeqDataBuffer[0] = command;
        cmdSeqDataBuffer[1] = (byte) sequenceNum;
        System.arraycopy(dataBuffer, 0, cmdSeqDataBuffer, 2, dataBuffer.length);
        Log.d(TAG, "cmdSeqDataBuffer is prepared: "+Arrays.toString(cmdSeqDataBuffer));
        Log.d(TAG, "Size of cmdSeqDataBuffer: "+cmdSeqDataBuffer.length);
        byte sum = DataTypeConverter.sum(cmdSeqDataBuffer);
        Log.d(TAG, "Sum by user: "+sum);
        Log.d(TAG, "Sum is received: "+dataSum);
        if(Math.abs(dataSize) != Math.abs(dataSizeSum)){
            Log.d(TAG, "Can not parse due to DataSize is not equal with Data Size Sum");
            return false;
        }
        if((cmdSeqDataBuffer.length != Math.abs(dataSize))) {
            Log.d(TAG, "Data Size is not equal with size of data size");
            return false;
        }
        if(Math.abs(sum) != Math.abs(dataSum)) {
            Log.d(TAG, "Can not parse due to Data Size incorrectly");
            return false;
        }
        return true;
    }
}
