package com.example.jetpackcomposeexample.utils;

import static androidx.core.content.ContextCompat.startActivity;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.util.Log;

import androidx.core.content.FileProvider;

import com.example.jetpackcomposeexample.BuildConfig;

import java.io.File;

public class ApkInstaller {

    public static boolean installAPK(Context context, File apkFile) {
        try {
            PackageManager packageManager = context.getPackageManager();
            PackageInfo packageInfo = packageManager.getPackageInfo(context.getPackageName(), 0);
            String versionName = packageInfo.versionName;

            Log.d("ApkInstaller", "Current Version Name: " + versionName);
            if (apkFile.exists()) {
                String apkFilePath = apkFile.getAbsolutePath();

                // Check versionName from APK
                PackageManager pm = context.getPackageManager();
                PackageInfo packageAPKInfo = pm.getPackageArchiveInfo(apkFilePath, 0);
                String versionAPKName = "";
                if (packageAPKInfo != null) {
                    versionAPKName = packageAPKInfo.versionName;
                    Log.d("ApkInstaller", "New Version APK Name: " + versionAPKName);
                } else {
                    Log.e("ApkInstaller", "Unable to retrieve APK version info.");
                }

                if(versionAPKName.equals(versionName)) {
                    Log.e("ApkInstaller", "New Version does not find");
                    return false;
                }
                Log.e("ApkInstaller", "Installing new version...");
                Uri apkUri = FileProvider.getUriForFile(
                        context,
                        BuildConfig.APPLICATION_ID + ".provider",
                        apkFile
                );

                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setDataAndType(apkUri, "application/vnd.android.package-archive");
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION); // Grant permission
                context.startActivity(intent);
                return true;
            }
            Log.e("ApkInstaller", "apk file does not exists");
        } catch (Exception e) {
            Log.e("ApkInstaller", "Exception when install version app "+e);

        }
        return false;
    }
}
