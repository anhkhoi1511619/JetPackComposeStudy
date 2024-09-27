package com.example.jetpackcomposeexample.controller.startup;

import android.content.Context;

import com.example.jetpackcomposeexample.utils.ApkInstaller;

import java.io.File;

public class DownloadProfile extends Job{
    Context context;

    public DownloadProfile(Context context) {
        super(context);
        this.context = context;
    }

    @Override
    protected void doRun() {
        File file = new File("/storage/emulated/0/log/Server/download/app/app-debug.apk");
        ApkInstaller.installAPK(context, file);
        setStatus(Status.DONE);
    }
}
