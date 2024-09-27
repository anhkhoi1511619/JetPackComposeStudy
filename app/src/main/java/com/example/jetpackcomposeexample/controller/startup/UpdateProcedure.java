package com.example.jetpackcomposeexample.controller.startup;

import android.content.Context;
import android.util.Log;

public class UpdateProcedure extends Job{
    DownloadProfileList downloadProfileList;
    DownloadProfile downloadProfile;
    UploadLog uploadLog;
    public UpdateProcedure(Context context) {
        super(context);
        downloadProfileList = new DownloadProfileList(context);
        downloadProfile = new DownloadProfile(context);
        uploadLog = new UploadLog(context);

        downloadProfileList
                .chain(downloadProfile, ChainCondition.RUN_IF_SUCCESS)
                .chain(uploadLog, ChainCondition.RUN_ALWAYS)
                .then(() -> {
                    Log.d(TAG, "downloadProfileList: "+downloadProfileList.status);
                    Log.d(TAG, "downloadProfile:"+downloadProfile.status);
                    Log.d(TAG, "uploadLog:"+uploadLog.status);
                });
    }

    @Override
    protected void doRun() {
        downloadProfileList.run();
    }
}
