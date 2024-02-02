package com.example.jetpackcomposeexample.utils;

import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.text.DateFormat;
import java.util.Arrays;
import java.util.List;

public class CsvLogger {
    static final String TAG = CsvLogger.class.getSimpleName();
    private int idx = 1;
    private File file;
    public CsvLogger(String fileName, String[] cols) {
        File folder = new File("/sdcard/postApp");
        boolean isFolderExists = folder.mkdirs();
        if(isFolderExists) {
            Log.d(TAG, "Folder is created successfully");
        } else {
            Log.d(TAG, "Error when creating folder");
        }
        file = new File("/sdcard/postApp", fileName);
        if(file.exists())return;

        try {
            FileOutputStream fos = new FileOutputStream(file, true);
            for (String header : cols) {
                fos.write((","+header).getBytes());
            }
            fos.write(("\r\n").getBytes());
            fos.close();
        } catch (Exception e) {

        }
    }

    public void pushRawString(List<String> data) {
        try {
            FileOutputStream fos = new FileOutputStream(file, true);
            String line = String.join(",", data);
            fos.write((line+"\r\n").getBytes());
            fos.close();
            idx++;
        } catch (Exception e) {

        }
    }
}
