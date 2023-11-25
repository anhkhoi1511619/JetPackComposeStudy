package com.example.jetpackcomposeexample.controller;

import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;
import android.util.Log;

import com.example.jetpackcomposeexample.model.helper.AwsConnectHelper;

public class AwsDataController {
    static final String TAG = AwsDataController.class.getSimpleName();
    static Handler controllerHandler = new Handler();
    static final int AWS_POST_API = 1;
    static final int AWS_CHART_API = 2;
    public static void startListener() {
        HandlerThread handlerThread = new HandlerThread("AwsDataController Handler Thread");
        handlerThread.start();
        controllerHandler = new Handler(handlerThread.getLooper()) {
            @Override
            public void handleMessage(Message msg) {
                int cmd = (int) msg.what;
                Log.d(TAG, "cmd receive :" + cmd);

                switch (cmd) {
                    case AWS_POST_API:
                        AwsConnectHelper.connect("https://192.168.180.42:9001/carList");
                        break;
                    case AWS_CHART_API:
                    default:
                        break;
                }
            }
        };
    }

    public static void sendMessage(int cmd){
        Message message = Message.obtain();
        message.what = cmd;
        controllerHandler.sendMessage(message);
    }
}
