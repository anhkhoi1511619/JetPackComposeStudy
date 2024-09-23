package com.example.jetpackcomposeexample.controller.startup;

import android.content.Context;

public class DownloadProfileList extends Job{

    @Override
    protected void doRun() {
        setStatus(Status.DONE);
    }
}
