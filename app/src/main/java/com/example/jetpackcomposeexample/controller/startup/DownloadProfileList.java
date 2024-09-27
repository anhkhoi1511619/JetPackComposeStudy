package com.example.jetpackcomposeexample.controller.startup;

import android.content.Context;

public class DownloadProfileList extends Job{

    public DownloadProfileList(Context context) {
        super(context);
    }

    @Override
    protected void doRun() {
        setStatus(Status.DONE);
    }
}
