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
        setStatus(Status.DONE);
    }
}
