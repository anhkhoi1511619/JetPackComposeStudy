package com.example.jetpackcomposeexample.controller.startup;

import android.content.Context;
import android.os.Build;
import android.util.Log;

import androidx.annotation.RequiresApi;

import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
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
        retry(uploadLog.done());
//        callback.accept(status);
    }

    public static boolean isScreenToRun = true;
    ScheduledFuture<?> runningTask;
    static int retryCount = 0;

    synchronized void increment() {
        retryCount++;
    }
    final int MAX_RETRY = 5;
    @RequiresApi(api = Build.VERSION_CODES.N)
    void retry(boolean isSuccess) {
        if(!isSuccess) {
            Log.d(TAG, "retry: "+retryCount+"/"+MAX_RETRY);
            if(retryCount>MAX_RETRY) {
                Log.d(TAG, "over 5 times. Stop");
                retryCount = 0;
                if(runningTask!=null) runningTask.cancel(true);
                return;
            }
            increment();
            runningTask = executor.scheduleAtFixedRate(()->{
                if (isScreenToRun) {
                    new UpdateProcedure(context).run();
                    runningTask.cancel(true);
                }
            },10, 10, TimeUnit.SECONDS);
        }
    }

    @Override
    protected void doRun() {
        getUpdateFiles.run();
    }
}
