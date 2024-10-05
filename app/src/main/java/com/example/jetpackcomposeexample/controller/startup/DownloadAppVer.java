package com.example.jetpackcomposeexample.controller.startup;

import android.content.Context;
import android.util.Log;

import com.example.jetpackcomposeexample.controller.server.AwsConnectHelper;
import com.example.jetpackcomposeexample.controller.startup.repository.SoftwarePackageRepository;
import com.example.jetpackcomposeexample.utils.FileTransferUtils;

import java.util.ArrayList;
import java.util.List;

public class DownloadAppVer extends Job{
    List<Status> downloadStatus = new ArrayList<>();
    public DownloadAppVer(Context context) {
        super(context);
    }

    @Override
    protected void doRun() {
        Log.d(TAG, "Downloading app data...");
        var versions = ((DownloadAplVerList)previous).versions;
        if(versions == null || versions.isEmpty()) {
            setStatus(Status.SKIPPED);
            return;
        }
        var helper = AwsConnectHelper.getInstance();
        for (var v : versions) {
            Log.d(TAG, "[DOWNLOAD] app data downloading... " + v.id + " url = " + v.url);
            if(v.url == null || !v.url.startsWith("http")) {
                downloadStatus.add(Status.DONE);
                continue;
            }
            var success = helper.download(v.url, SoftwarePackageRepository.TMP_PACKAGE_PATH+"/"+v.id);
            if(!success) {
                Log.d(TAG, "[DOWNLOAD] app data download failed! "+v.id);
                downloadStatus.add(Status.FAILED);
            } else {
                Log.d(TAG, "[DOWNLOAD] app data download success " + v.id);
                downloadStatus.add(Status.DONE);
            }

            if(done(downloadStatus)) {
                Log.i(TAG, "copying downloaded app files from tmp...");
                FileTransferUtils.syncArchive(SoftwarePackageRepository.TMP_PACKAGE_PATH,
                        SoftwarePackageRepository.UPDATE_PACKAGE_PATH);
                setStatus(Status.DONE);
            } else {
                Log.d(TAG, "download app failed somewhere !");
                setStatus(Status.FAILED);
            }
        }
    }
}
