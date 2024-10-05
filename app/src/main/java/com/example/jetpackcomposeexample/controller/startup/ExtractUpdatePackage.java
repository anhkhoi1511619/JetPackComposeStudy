package com.example.jetpackcomposeexample.controller.startup;

import android.content.Context;

import com.example.jetpackcomposeexample.controller.startup.repository.SoftwarePackageRepository;
import com.example.jetpackcomposeexample.utils.FileTransferUtils;
import com.example.jetpackcomposeexample.utils.TLog;

import java.io.File;

public class ExtractUpdatePackage extends Job{
    public ExtractUpdatePackage(Context context) {
        super(context);
    }

    @Override
    public void clear() {
        super.clear();
        FileTransferUtils.removeDirectory(SoftwarePackageRepository.TMP_PACKAGE_PATH);

    }

    @Override
    protected void doRun() {
        var success = extractControllerApp();
        setStatus(success ? Status.DONE : Status.FAILED);
    }
    boolean extractControllerApp() {
        for (var software : SoftwarePackageRepository.DataType.values()) {
            if(!SoftwarePackageRepository.hasNewPackage(software)){
                TLog.d(TAG, "No new package at "+ software.getLocation()+", skip extracting");
                return true;
            }
            TLog.d(TAG, "Extracting archive in: "+ software.getLocation());
            FileTransferUtils.emptyDirectory(software.getTmpLocation());
            if(!extractAllArchive(software.getLocation(), software.getTmpLocation())) return false;
            FileTransferUtils.copyDirectory(software.getTmpLocation(), software.getLocation());
            SoftwarePackageRepository.markedPackageResolved(software);
        }
        return true;
    }
    boolean extractAllArchive(String inputFolder, String outputFolder) {
        var entry = new File(inputFolder);
        var children = entry.listFiles((e) ->
                e.getName().toLowerCase().endsWith(".tar.gz"));
        if(children != null) {
            for(var file: children) {
                if(!FileTransferUtils.extractTarGz(file.getAbsolutePath(), outputFolder)) {
                    if(file.delete()) {
                        TLog.d(TAG, "can't extract "+file.getName()+", delete file");
                    } else {
                        TLog.d(TAG, "can't extract "+file.getName()+", but can not delete file");
                    }
                    return false;
                }
            }
        }
        children = entry.listFiles((e) ->
                e.getName().toLowerCase().endsWith(".zip"));
        if(children != null) {
            for (var file : children) {
                if (!FileTransferUtils.extractZip(file.getAbsolutePath(), outputFolder)) {
                    if(file.delete()) {
                        TLog.d(TAG, "can't extract "+file.getName()+", delete file");
                    } else {
                        TLog.d(TAG, "can't extract "+file.getName()+", but can not delete file");
                    }
                    return false;
                }
            }
        }
        return true;
    }
}
