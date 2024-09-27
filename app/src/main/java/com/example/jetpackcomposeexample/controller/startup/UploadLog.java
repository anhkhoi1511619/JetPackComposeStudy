package com.example.jetpackcomposeexample.controller.startup;

import static com.example.jetpackcomposeexample.utils.UrlConstants.GET_URL_API_OKHTTP;

import android.content.Context;
import android.util.Log;

import com.example.jetpackcomposeexample.controller.server.AwsConnectHelper;
import com.example.jetpackcomposeexample.model.getUrl.GetUrlRequest;
import com.example.jetpackcomposeexample.model.getUrl.GetUrlResponse;

public class UploadLog extends Job{

    public UploadLog(Context context) {
        super(context);
    }

    @Override
    protected void doRun() {
        int machineId = 960;
        GetUrlRequest request = new GetUrlRequest(machineId);

        GetUrlResponse response = AwsConnectHelper.getInstance().getUrl(GET_URL_API_OKHTTP, request);
        Log.d(TAG, "Url: "+response.url);
        if(response.url == null) setStatus(Status.FAILED);
        boolean success = AwsConnectHelper.getInstance().uploadLogOkHttp(response.url, "/storage/self/primary/log/operation_log.tar.gz");
        Log.d(TAG, "success: "+success);
        setStatus(success ? Status.DONE : Status.FAILED);
    }
}
