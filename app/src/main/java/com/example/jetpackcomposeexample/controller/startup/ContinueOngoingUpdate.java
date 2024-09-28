package com.example.jetpackcomposeexample.controller.startup;

import android.content.Context;

import com.example.jetpackcomposeexample.utils.ApkInstaller;

import java.io.File;

public class ContinueOngoingUpdate extends Job{
    public ContinueOngoingUpdate(Context context) {
        super(context);
    }

    @Override
    protected void doRun() {
        File file = new File("/storage/emulated/0/log/Server/download/app/app-debug.apk");
        boolean isUpdating = ApkInstaller.installAPK(context, file);
        setStatus(isUpdating ? Status.PENDING : Status.DONE);
    }
}
