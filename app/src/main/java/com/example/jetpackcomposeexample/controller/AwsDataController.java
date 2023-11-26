package com.example.jetpackcomposeexample.controller;


import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;
import android.util.Log;

import com.example.jetpackcomposeexample.R;
import com.example.jetpackcomposeexample.model.helper.AwsConnectHelper;
import com.example.jetpackcomposeexample.model.helper.dto.MetaData;
import com.example.jetpackcomposeexample.model.helper.dto.Paragraph;
import com.example.jetpackcomposeexample.model.helper.dto.Post;
import com.example.jetpackcomposeexample.model.helper.dto.PostAuthor;
import com.example.jetpackcomposeexample.model.helper.dto.Publication;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class AwsDataController {
    static final String TAG = AwsDataController.class.getSimpleName();
    static Handler controllerHandler = new Handler();
    public static final int AWS_POST_API = 1;
    public static final int AWS_POST_API_RESPONSE = 2;
    static final int AWS_CHART_API = 3;
    public static Post post;
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
                    case AWS_POST_API_RESPONSE:
                        post =  parse((JSONObject) msg.obj);
                        Log.d(TAG,"real data: "+msg.obj.toString());
                        Log.d(TAG,"data after parsed: "+post.toString());
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
    static Post parse(JSONObject jsonObject) {
        String id_data;
        String title_data;
        String subtitle_data;
        String url;
        JSONObject publication;
        String name_publication;
        String url_publication;
        try {
            id_data = (String) jsonObject.get("id");
            title_data =  (String) jsonObject.get("title");
            subtitle_data = (String) jsonObject.get("subtitle");
            url = (String) jsonObject.get("url");
            publication = (JSONObject) jsonObject.get("publication");
            name_publication = (String) publication.get("name");
            url_publication = (String) publication.get("url");
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }

        List<Paragraph> paragraphs = new ArrayList<>();

        return new Post(
                id_data,
                title_data,
                subtitle_data,
                url,
                new Publication(
                        name_publication,
                        url_publication
                ),
                new MetaData(
                        new PostAuthor(
                                "Florina Muntenescu",
                                "https://medium.com/@florina.muntenescu"
                        ),
                        "July 09",
                        1
                ),
                paragraphs,
                R.drawable.post_3,
                R.drawable.post_3_thumb
                );
    }



}
