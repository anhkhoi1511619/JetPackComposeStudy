package com.example.jetpackcomposeexample.controller.startup;

import android.content.Context;
import android.util.Log;

import java.util.function.Consumer;

public class UpdateProcedure extends Job{
    GetUpdateFiles getUpdateFiles;
    UpdateControllerApp updateControllerApp;
    UploadLog uploadLog;
    static Consumer<Status> callback;

    public static void setCallback(Consumer<Status> callback) {
        UpdateProcedure.callback = callback;
    }

    public UpdateProcedure(Context context) {
        super(context);
        getUpdateFiles = new GetUpdateFiles(context);
        updateControllerApp = new UpdateControllerApp(context);
        uploadLog = new UploadLog(context);
        getUpdateFiles
                .chain(updateControllerApp, ChainCondition.RUN_ALWAYS)
                .chain(uploadLog, ChainCondition.RUN_ALWAYS)
                .then(this::report);
    }
    void report() {
        Log.d(TAG, "getUpdateFiles:"+getUpdateFiles.status);
        Log.d(TAG, "updateControllerApp: "+updateControllerApp.status);
        Log.d(TAG, "uploadLog:"+uploadLog.status);
        if(updateControllerApp.status == Status.PENDING)  {
            setStatus(Status.PENDING);
        } else if (!getUpdateFiles.done()) {
            setStatus(Status.FAILED);
        } else {
            setStatus(Status.DONE);
        }
        callback.accept(status);
    }

    @Override
    protected void doRun() {
        getUpdateFiles.run();
    }
}
