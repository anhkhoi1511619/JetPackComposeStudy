package com.example.jetpackcomposeexample.model.bus.dto;

public class BusData {
    BusRequest request;
    BusResponse response;
    public void deserialize(byte[] data) {
        //TODO: Deserialize raw data to BusRequest data
    }
    public byte[] serialize() {
        //TODO: From raw BusRequest data to BusResponse data
        return new byte[]{2,1,1,3};
    }
}
