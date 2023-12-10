package com.example.jetpackcomposeexample.controller;


import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;
import android.util.Log;

import com.example.jetpackcomposeexample.aws.helper.AwsConnectHelper;
import com.example.jetpackcomposeexample.model.helper.AwsDataModel;

import org.json.JSONObject;

public class AwsDataController {
    static final String TAG = AwsDataController.class.getSimpleName();
    static Handler controllerHandler = new Handler();
    public static final String POST_CONTENT_API_URL = "http://192.168.180.42:9000/postContent";
    public static final String CHART_API_URL = "https://192.168.180.42:9001/messageRequestt";
    public static final int AWS_POST_API = 1;
    public static final int AWS_POST_API_RESPONSE = 2;
    static final int AWS_CHART_API = 3;
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
                        AwsConnectHelper.connect(POST_CONTENT_API_URL);
                        break;
                    case AWS_POST_API_RESPONSE:
                        AwsDataModel.parsePostContent((JSONObject) msg.obj);
                        Log.d(TAG,"real data: "+msg.obj.toString());
                        Log.d(TAG,"data after parsed: "+AwsDataModel.post.toString());
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

    public static void sendMessage(int cmd, Object object){
        Message message = Message.obtain();
        message.what = cmd;
        message.obj = object;
        controllerHandler.sendMessage(message);
    }
}
