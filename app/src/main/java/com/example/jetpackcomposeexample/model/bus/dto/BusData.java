package com.example.jetpackcomposeexample.model.bus.dto;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.Calendar;

public class BusData {

    public class Data {
        int staffId; // 3 bytes
        int routeId; // BCD
        int sendTime; // 3 bytes BCD, HHmmSS
        short stopSequence; // BCD
        short signalStrength; // range: -140 ~ -44
        public void deserialize(byte[] data) {
            int offset = 0;

            offset+=7;
            byte[] staffIdArr = Arrays.copyOfRange(data, offset, offset+3);
            staffId = ByteBuffer.wrap(staffIdArr).getInt();
            offset+=3;
            byte[] routeIdArr = Arrays.copyOfRange(data, offset, offset+4);
            routeId = ByteBuffer.wrap(routeIdArr).getInt();
            offset+=4;
            byte[] sendTimeArr = Arrays.copyOfRange(data, offset, offset+3);
            sendTime = ByteBuffer.wrap(sendTimeArr).getInt();
            offset+=3;
            byte[] stopSequenceArr = Arrays.copyOfRange(data, offset, offset+3);
            stopSequence = ByteBuffer.wrap(stopSequenceArr).getShort();
            offset+=3;
            byte[] signalStrengthArr = Arrays.copyOfRange(data, offset, offset+2);
            signalStrength = ByteBuffer.wrap(data).getShort();
        }
    }
    byte stx =  0x02;
    short dataSize;
    byte dataSizeSum;
    byte command = 0x00;
    static int sequenceNum = 1; // 1 byte
    public Data data = new Data();
    byte dataSum;
    byte etx = 0x03;

    public byte[] serialize() {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        //TODO: Use data to check
        //Debug
        dataSize = 0xf0;
        dataSizeSum = 0x10;
        dataSum = 0x09;
        command = 0x01;
        sequenceNum = 0x01;

        stream.write(stx);
        stream.write(dataSize);
        stream.write(dataSizeSum);
        stream.write(command);
        stream.write(sequenceNum);
        stream.write(dataSum);
        stream.write(etx);

        return stream.toByteArray();
    }
}
