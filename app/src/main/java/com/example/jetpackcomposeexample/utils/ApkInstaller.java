package com.example.jetpackcomposeexample.utils;

import static androidx.core.content.ContextCompat.startActivity;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.util.Log;

import androidx.core.content.FileProvider;

import com.example.jetpackcomposeexample.BuildConfig;

import java.io.File;

public class ApkInstaller {

    public static void installAPK(Context context, File apkFile) {
        try {
            if (apkFile.exists()) {
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
                return;
            }
            Log.d("TAG", "apk file does not exists");
        } catch (Exception e) {
            Log.e("TAG", "Exception when install version app "+e);

        }
    }
}
