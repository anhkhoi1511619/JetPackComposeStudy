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
        File folder = new File("/sdcard/DCIM/ProfileApp");
        boolean isFolderExists = folder.mkdirs();
        if(isFolderExists) {
            Log.d(TAG, "Folder is created successfully");
        } else {
            Log.d(TAG, "Folder is exists");
        }
        file = new File("/sdcard/DCIM/ProfileApp/"+fileName);
        if(file.exists())return;

        try {
            if (file.createNewFile()) {
                Log.d(TAG, "File created: " + file.getName());
            } else {
                Log.d(TAG, "File already exists.");
            }
            FileOutputStream fos = new FileOutputStream(file, true);
            for (String header : cols) {
                fos.write((","+header).getBytes());
            }
            fos.write(("\r\n").getBytes());
            fos.close();
            Log.d(TAG, "Write header in File is done");
        } catch (Exception e) {
            Log.d(TAG, "Error when writing header in file");
        }
    }

    public void pushRawString(List<String> data) {
        try {
            FileOutputStream fos = new FileOutputStream(file, true);
            String line = String.join(",", data);
            fos.write((line+"\r\n").getBytes());
            fos.close();
            idx++;
            Log.d(TAG, "Write content in File is done");
        } catch (Exception e) {
            Log.d(TAG, "Error when writing content in file");
        }
    }
}
