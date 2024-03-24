package com.example.jetpackcomposeexample.model.train.dto;

import android.util.Log;

import com.example.jetpackcomposeexample.model.train.factory.Send;


public class MainControllerResponse extends TrainCommPackageDTO implements Send {
    final String TAG = "SubDataResponse";
    public boolean isHandShake;


    @Override
    public byte[] serialize() {
        try {
            data = new byte[]{(byte) (isHandShake ? 0x01 : 0x00)};
            return super.serialize();
        } catch (Exception e) {
            return new byte[]{0};
        }
    }

    @Override
    public void deserialize(byte[] data) {
        if (data == null) return;
        super.deserialize(data);
        data = super.data;
        Log.d(TAG, "SubDataResponse Data Ready to deserialize");
        isHandShake = data[0] == 1;
        Log.d(TAG, "isHandShake: " + isHandShake);
    }
}
