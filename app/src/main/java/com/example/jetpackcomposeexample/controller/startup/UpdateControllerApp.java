package com.example.jetpackcomposeexample.controller.startup;

import android.content.Context;

import com.example.jetpackcomposeexample.utils.ApkInstaller;

import java.io.File;

public class UpdateControllerApp extends Job{
    public UpdateControllerApp(Context context) {
        super(context);
    }

    @Override
    protected void doRun() {
        File file = new File("/storage/emulated/0/log/Server/download/app/11/app-debug.apk");
        boolean isUpdating = ApkInstaller.installAPK(context, file);
        setStatus(isUpdating ? Status.PENDING : Status.DONE);
    }
}
