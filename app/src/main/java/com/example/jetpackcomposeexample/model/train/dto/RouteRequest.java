package com.example.jetpackcomposeexample.model.train.dto;

import android.util.Log;


import com.example.jetpackcomposeexample.model.train.factory.Send;
import com.example.jetpackcomposeexample.utils.TrainDataUtils;

import java.util.Arrays;

public class RouteRequest extends TrainCommPackageDTO implements Send {
    final String TAG = "RouteDataRequest";
    public int currentRouteId;

    @Override
    public byte[] serialize() {
        try {
            data = TrainDataUtils.toBytes(currentRouteId, 8);
            return super.serialize();
        } catch (Exception e) {
            return new byte[]{0};
        }
    }
    public static int getCurrentRouteId(TrainCommPackageDTO dto) {
        return TrainDataUtils.castInt(dto.getData(), 0,8);
    }
    @Override
    public void deserialize(byte[] data) {
        if(data == null) return;
        super.deserialize(data);
        data = super.data;
        Log.d(TAG, "MainRouteRequest Data Ready to deserialize");
        currentRouteId = TrainDataUtils.castInt(data, 0,8);
        Log.d(TAG, "routeIdArr: "+Arrays.toString(data)+"     currentRouteId: "+currentRouteId);
    }
}
