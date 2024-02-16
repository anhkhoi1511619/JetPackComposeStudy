package com.example.jetpackcomposeexample.model.bus.dto;

import android.util.Log;

import com.example.jetpackcomposeexample.utils.DataTypeConverter;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.Calendar;

public class BusDataRequest extends CommPackageDTO {
    static final String TAG = BusDataRequest.class.getSimpleName();

    public class BusData {
        int staffId; // 3 bytes
        int routeId; // BCD
        Calendar sendTime = Calendar.getInstance(); // 3 bytes BCD, HHmmSS
        int stopSequence; // BCD
        int signalStrength; // range: -140 ~ -44
        public void deserialize(byte[] data) {
            Log.d(TAG, "Bus Data Ready to deserialize");
            int offset = 0;
            byte[] staffIdArr = Arrays.copyOfRange(data, offset, offset+3);
            Log.d(TAG, "staffIdArr: "+Arrays.toString(staffIdArr));
            staffId = DataTypeConverter.castInt(staffIdArr);
            Log.d(TAG, "staffId: "+staffId);
            offset+=3;
            byte[] routeIdArr = Arrays.copyOfRange(data, offset, offset+4);
            routeId = DataTypeConverter.castIntFromBCD(routeIdArr);
            Log.d(TAG, "routeIdArr: "+Arrays.toString(routeIdArr)+"     routeId: "+routeId);
            offset+=4;
            byte[] sendTimeArr = Arrays.copyOfRange(data, offset, offset+3);
            int hour = sendTimeArr[0];
            int minutes = sendTimeArr[1];
            int seconds = sendTimeArr[2];
            DataTypeConverter.bcdToTime(sendTimeArr, sendTime);
            Log.d(TAG, "sendTimeArr: "+Arrays.toString(sendTimeArr)+"     sendTime: "+hour+":"+minutes+":"+seconds);
            offset+=3;
            byte[] stopSequenceArr = Arrays.copyOfRange(data, offset, offset+2);
            stopSequence = DataTypeConverter.castIntFromBCD(stopSequenceArr);
            Log.d(TAG, "stopSequenceArr: "+Arrays.toString(stopSequenceArr)+"     stopSequence: "+stopSequence);
            offset+=2;
            byte[] signalStrengthArr = Arrays.copyOfRange(data, offset, offset+2);
            signalStrength = DataTypeConverter.castInt(signalStrengthArr);
            Log.d(TAG, "signalStrengthArr: "+Arrays.toString(signalStrengthArr)+"     signalStrength: "+signalStrength);
        }
        public String toString() {
            StringBuilder ret = new StringBuilder();
            for (Field field : this.getClass().getDeclaredFields()) {
                try {
                    Object val = field.get(this);
                    String valStr = val != null ? val.toString() : "null";
                    if(field.getType() == Calendar.class) valStr = "...";
                    ret.append(field.getName()).append(": ").append(valStr).append(",");
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            return ret.toString();
        }
    }
    public BusData busData = new BusData();

    @Override
    protected void doRun() {
        busData.deserialize(data);
        Log.d(TAG, "Data: "+ busData.toString());
    }
}
