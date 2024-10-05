package com.example.jetpackcomposeexample.controller.startup;

import android.content.Context;

public class GetUpdatePackageRemote extends Job{
    CheckInternet checkInternet;
    DownloadAplVerList downloadAplVerList;
    DownloadAppVer downloadAppVer;
    DownloadProfileList downloadProfileList;
    DownloadProfile downloadProfile;
    public GetUpdatePackageRemote(Context context) {
        super(context);
        checkInternet = new CheckInternet(context);
        downloadProfileList = new DownloadProfileList(context);
        downloadProfile = new DownloadProfile(context);
        downloadAplVerList = new DownloadAplVerList(context);
        downloadAppVer = new DownloadAppVer(context);
        checkInternet
                .chain(downloadProfileList, ChainCondition.RUN_IF_SUCCESS)
                .chain(downloadProfile, ChainCondition.RUN_IF_SUCCESS)
                .chain(downloadAplVerList, ChainCondition.RUN_IF_SUCCESS)
                .chain(downloadAppVer, ChainCondition.RUN_IF_SUCCESS)
                .then(()->{
                    var success = checkInternet.done() &&
                            downloadAplVerList.done() &&
                            downloadAppVer.done() &&
                            downloadProfileList.done() &&
                            downloadProfile.done();
                    setStatus(success ? Status.DONE : Status.FAILED);
                });

    }

    @Override
    protected void doRun() {
        checkInternet.run();
    }
}
