package com.example.jetpackcomposeexample.utils;

import java.util.Calendar;

public class DataTypeConverter {
    public static int castInt(byte[] input) {
        return castInt(input, 0, input.length);
    }
    public static int castInt(byte[] input, int offset, int length) {
        int ret = 0;
        int n = Math.min(offset+length, input.length);
        for (int i = offset; i < n; i++) {
            ret = ret<<8 | (0xff & (int)input[i]);
        }
        return ret;
    }

    public static int castIntFromBCD(byte[] input) {
        int ret = 0;
        for (byte x : input) {
            ret = ret*100 + (x>>4 & 0x0f)*10 + (x & 0x0f);
        }
        return ret;
    }
    public static Calendar bcdToTime(byte[] input, Calendar output) {
        if(input.length>3) return output;
        int hour = input[0];
        int minutes = input[1];
        int seconds = input[2];
        output.set(Calendar.HOUR_OF_DAY, hour);
        output.set(Calendar.MINUTE, minutes);
        output.set(Calendar.SECOND, seconds);
        return output;
    }
    public static byte[] toBytes(int input, int size) {
        byte[] ret = new byte[size];
        for (int i = 0; i < size; i++) {
            ret[size-i-1] = (byte) ((input>>(i*8)) & 0xff);
        }
        return ret;
    }
    public static byte sum(byte[] input) {
        byte ret = 0;
        for(byte x: input) ret += x;
        return ret;
    }
}
