package com.example.jetpackcomposeexample.model.train.dto;

import android.util.Log;

import com.example.jetpackcomposeexample.model.train.factory.Send;


public class SubDirectionRequest extends TrainCommPackageDTO implements Send {
    final String TAG = "SubDirectionRequest";
    public byte direction;
    public byte controllerNumber;

    @Override
    public byte[] serialize() {
        try {
            data =  new byte[]{direction, controllerNumber};
            return super.serialize();
        } catch (Exception e) {
            return new byte[]{0};
        }
    }
    @Override
    public void deserialize(byte[] data) {
        if(data == null) return;
        super.deserialize(data);
        data = super.data;
        Log.d(TAG, "MainDirectionRequest Data Ready to deserialize");
        direction = data[0];
        Log.d(TAG, "direction: "+direction);
        controllerNumber = data[1];
        Log.d(TAG, "controllerNumber: "+controllerNumber);
    }
}
