package com.example.jetpackcomposeexample.model.bus.dto;

import com.example.jetpackcomposeexample.utils.DataTypeConverter;

public class BusDataResponse extends CommPackageDTO {
    int data;

    public void setData(int data) {
        this.data = data;
    }

    @Override
    protected void doRun() {
        setData(data);
    }
}
