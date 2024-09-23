package com.example.jetpackcomposeexample.controller.startup;

import android.content.Context;

public class DownloadProfile extends Job{

    @Override
    protected void doRun() {
        setStatus(Status.DONE);
    }
}
