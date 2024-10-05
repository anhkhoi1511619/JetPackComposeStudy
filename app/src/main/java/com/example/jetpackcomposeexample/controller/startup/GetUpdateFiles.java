package com.example.jetpackcomposeexample.controller.startup;

import android.content.Context;

import com.example.jetpackcomposeexample.utils.TLog;

public class GetUpdateFiles extends Job{
    GetUpdatePackageSomewhere getUpdatePackageSomewhere;
    ExtractUpdatePackage extractUpdatePackage;
    ServerUpdatePackageFTP serverUpdatePackageFTP;
    int retryCount = 1;
    final int RETRY_MAX = 5;
    public GetUpdateFiles(Context context) {
        super(context);
        getUpdatePackageSomewhere = new GetUpdatePackageSomewhere(context);
        extractUpdatePackage = new ExtractUpdatePackage(context);
        serverUpdatePackageFTP = new ServerUpdatePackageFTP(context);
        getUpdatePackageSomewhere
                .chain(extractUpdatePackage, ChainCondition.RUN_IF_STRICTLY_SUCCESS)
                .then(() -> {
                    TLog.d(TAG, "getUpdatePackage & extractUpdatePackage round "+retryCount+" finished");
                    var success = extractUpdatePackage.done();
                    if(!success && retryCount < RETRY_MAX) {
                        getUpdatePackageSomewhere.resetStatus();
                        extractUpdatePackage.resetStatus();
                        executor.execute(()-> getUpdatePackageSomewhere.run());
                    }
                })
                .chain(serverUpdatePackageFTP, ChainCondition.RUN_ALWAYS)
                .then(()->{
                    setStatus(extractUpdatePackage.done() ? Status.DONE : Status.FAILED);
                });
    }

    @Override
    protected void doRun() {
        getUpdatePackageSomewhere.run();
    }
}
