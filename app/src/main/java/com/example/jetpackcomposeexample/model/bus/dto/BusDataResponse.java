package com.example.jetpackcomposeexample.model.bus.dto;

public class BusDataResponse extends CommPackageDTO {
    int data;

    @Override
    protected void replace() {
        setData(new byte[]{0});
    }
}
