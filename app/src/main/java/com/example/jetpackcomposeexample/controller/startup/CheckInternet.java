package com.example.jetpackcomposeexample.controller.startup;

import android.content.Context;

public class CheckInternet extends Job{
    public CheckInternet(Context context) {
        super(context);
    }

    @Override
    protected void doRun() {
        setStatus(Status.DONE);
    }
}
