package com.example.jetpackcomposeexample.model.bus.dto;

public class BusResponse {
    byte stx = 0x02;
    short dataSize;

    byte dataSizeSum;

    byte command = 0x00;

    static int sequenceNum = 1;

    byte dataSum;

    byte etx = 0x03;
}
