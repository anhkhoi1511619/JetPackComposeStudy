package com.example.jetpackcomposeexample.controller.startup;

import android.content.Context;

public class ServerUpdatePackageFTP extends Job{
    public ServerUpdatePackageFTP(Context context) {
        super(context);
    }

    @Override
    protected void doRun() {
        setStatus(Status.DONE);
    }
}
