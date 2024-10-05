package com.example.jetpackcomposeexample.controller.startup;

import android.content.Context;

public class GetUpdatePackageSomewhere extends Job{
    GetUpdatePackageRemote getUpdatePackageRemote;
    DownloadUpdatePackageFTP downloadUpdatePackageFTP;
    public GetUpdatePackageSomewhere(Context context) {
        super(context);
        getUpdatePackageRemote = new GetUpdatePackageRemote(context);
        downloadUpdatePackageFTP = new DownloadUpdatePackageFTP(context);
        getUpdatePackageRemote
                .chain(downloadUpdatePackageFTP, ChainCondition.RUN_IF_FAILED)
                .then(() -> {
                    var success = getUpdatePackageRemote.done() || downloadUpdatePackageFTP.done();
                    setStatus(success ? Status.DONE : Status.FAILED);
                });
    }

    @Override
    protected void doRun() {
        getUpdatePackageRemote.run();
    }
}
