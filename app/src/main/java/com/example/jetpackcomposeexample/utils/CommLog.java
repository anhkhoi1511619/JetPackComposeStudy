package com.example.jetpackcomposeexample.utils;

import android.util.Log;

public class CommLog {
    final static boolean SHOULD_DISPLAY = false;
    public static void d(String TAG, String content) {
        if(!SHOULD_DISPLAY) return;
        Log.d(TAG, content);
    }
    public static void e(String TAG, String content) {
        if(!SHOULD_DISPLAY) return;
        Log.e(TAG, content);
    }
}
