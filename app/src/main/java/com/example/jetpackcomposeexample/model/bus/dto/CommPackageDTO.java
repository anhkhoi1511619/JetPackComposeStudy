package com.example.jetpackcomposeexample.model.bus.dto;

import com.example.jetpackcomposeexample.utils.DataTypeConverter;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class CommPackageDTO {
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
}
