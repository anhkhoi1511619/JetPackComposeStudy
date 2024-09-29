package com.example.jetpackcomposeexample.controller.startup.repository;

import android.os.Build;
import android.os.Environment;
import android.util.Log;

import androidx.annotation.RequiresApi;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

public class AppPackageRepository {
    public enum DataType {
        PROFILE_APP("11");
        DataType(String s) {
            this.value = s;
        }

        public final String value;
        public String getLocation() {
            return UPDATE_PACKAGE_PATH+"/"+value;
        }
        public String getTmpLocation() {
            return TMP_PACKAGE_PATH+"/"+value;
        }
    }

    public static final String PATH = Environment.getExternalStorageDirectory().getAbsolutePath();
    public static final String TMP_PACKAGE_PATH = PATH+"/log/Server/download/app-tmp";
    public static final String UPDATE_PACKAGE_PATH  = PATH+"/log/Server/download/app";
    static final String SAVE_PATH = "AppVersion.csv";
    public static Map<String, String> data = new HashMap<>();

    @RequiresApi(api = Build.VERSION_CODES.N)
    public static void insertDefaultData() {
        for(DataType type: DataType.values()) {
            data.putIfAbsent(type.value, type.value+"_0001_20000101_001");
        }
    }

    public static void load() {
        insertDefaultData();
        try {
            var content = Files.lines(Paths.get(UPDATE_PACKAGE_PATH+"/"+SAVE_PATH))
                    .skip(1)
                    .findFirst()
                    .orElse("");
            if(content.isEmpty()) return;
            var packages = content.split(",");
            for (int i = 0;i<Math.min(packages.length, DataType.values().length);i++) {
                data.put(DataType.values()[i].value, packages[i]);
            }
        } catch (IOException e) {
            Log.e("AppPackageRepository", "error while reading package list "+e.getMessage());

        }
    }


}
