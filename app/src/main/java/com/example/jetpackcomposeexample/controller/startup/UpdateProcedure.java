package com.example.jetpackcomposeexample.controller.startup;

import android.content.Context;
import android.util.Log;

import com.example.jetpackcomposeexample.view.viewmodel.ScreenID;

import java.util.function.Consumer;

public class UpdateProcedure extends Job{
    ContinueOngoingUpdate continueOngoingUpdate;

    DownloadProfileList downloadProfileList;
    DownloadProfile downloadProfile;
    UploadLog uploadLog;
    static Consumer<Status> callback;

    public static void setCallback(Consumer<Status> callback) {
        UpdateProcedure.callback = callback;
    }

    public UpdateProcedure(Context context) {
        super(context);
        downloadProfileList = new DownloadProfileList(context);
        downloadProfile = new DownloadProfile(context);
        uploadLog = new UploadLog(context);
        continueOngoingUpdate = new ContinueOngoingUpdate(context);
        continueOngoingUpdate
                .chain(downloadProfileList, ChainCondition.RUN_IF_STRICTLY_SUCCESS)
                .chain(downloadProfile, ChainCondition.RUN_IF_SUCCESS)
                .chain(uploadLog, ChainCondition.RUN_ALWAYS)
                .then(this::report);
    }
    void report() {
        Log.d(TAG, "continueOngoingUpdate: "+continueOngoingUpdate.status);
        Log.d(TAG, "downloadProfileList: "+downloadProfileList.status);
        Log.d(TAG, "downloadProfile:"+downloadProfile.status);
        Log.d(TAG, "uploadLog:"+uploadLog.status);
        if(continueOngoingUpdate.status == Status.PENDING)  {
            setStatus(Status.PENDING);
        } else if (!downloadProfile.done() || !downloadProfileList.done()) {
            setStatus(Status.FAILED);
        } else {
            setStatus(Status.DONE);
        }
        callback.accept(status);
    }

    @Override
    protected void doRun() {
        continueOngoingUpdate.run();
    }
}
