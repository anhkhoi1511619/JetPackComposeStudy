package com.example.jetpackcomposeexample.controller.startup;

import static com.example.jetpackcomposeexample.utils.UrlConstants.APL_VER_API_OKHTTP;

import android.content.Context;
import android.util.Log;

import com.example.jetpackcomposeexample.controller.server.AwsConnectHelper;
import com.example.jetpackcomposeexample.controller.startup.repository.AppPackageRepository;
import com.example.jetpackcomposeexample.model.aplver.AppVersionRequest;
import com.example.jetpackcomposeexample.model.aplver.AppVersionResponse;

import java.util.List;

public class DownloadAplVerList extends Job{
    public List<AppVersionResponse.Version> versions = null;

    public DownloadAplVerList(Context context) {
        super(context);
    }

    @Override
    protected void doRun() {
        AppVersionRequest request = new AppVersionRequest(1511619);
        AppPackageRepository.load();
        AppPackageRepository.data.forEach(request::addVersion);
        AppVersionResponse response = AwsConnectHelper.getInstance().getAplVer(APL_VER_API_OKHTTP, request);
        try {
            if(response == null) {
                throw new Exception("Invalid result");
            }
            versions = response.appVersion;
            setStatus(Status.DONE);
        } catch (Exception e) {
            Log.e(TAG, "Error while getting master version list: "+e.getMessage());
            setStatus(Status.FAILED);
        }

    }
}
