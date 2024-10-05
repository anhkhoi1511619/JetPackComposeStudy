package com.example.jetpackcomposeexample.controller.startup.repository;

import android.os.Build;
import android.os.Environment;
import android.util.Log;

import androidx.annotation.RequiresApi;

import com.example.jetpackcomposeexample.utils.FileTransferUtils;
import com.example.jetpackcomposeexample.utils.TLog;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class SoftwarePackageRepository {
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
        public String toString() {
            switch (value) {
                case "11":
                    return "制御器アプリケーション";
            }
            return "";
        }
    }
    final static String TAG = SoftwarePackageRepository.class.getSimpleName();

    public static final String PATH = Environment.getExternalStorageDirectory().getAbsolutePath();
    public static final String TMP_PACKAGE_PATH = PATH+"/log/Server/download/app-tmp";
    public static final String UPDATE_PACKAGE_PATH  = PATH+"/log/Server/download/app";
    static final String SAVE_PATH = "AppVersion.csv";
    static final Pattern PACKAGE_NAME_PATTERN =
            Pattern.compile("^\\d{2}_\\d{4}_\\d{8}_\\d{3}$", Pattern.CASE_INSENSITIVE);
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
    public static boolean hasNewPackage(DataType software) {
        var current = data.getOrDefault(software.value,"");
        TLog.d(TAG, "checking new package for "+software.value+", current package = "+current);
        var entry = new File(software.getLocation());
        var children = entry.listFiles(FileTransferUtils::isArchive);
        if (children != null && children.length != 0) {
            var next = children[0].getName();
            next = next.substring(0, next.indexOf('.'));
            var same = next.equals(current);
            return !same;
        }
        TLog.d(TAG, "checking new package for "+software.value+", download folder has nothing");
        return false;
    }

    public static void markedPackageResolved(DataType software) {
        var entry = new File(software.getLocation());
        var children = entry.listFiles(FileTransferUtils::isArchive);
        if(children != null && children.length != 0) {
            var next = children[0].getName();
            next = next.substring(0, next.indexOf("."));
            var valid = PACKAGE_NAME_PATTERN.matcher(next).find();
            if(valid) {
                data.put(software.value, next);
            } else {
                Log.w(TAG, "Found invalid package name "+next
                        + ", ignore it, current package name is "+data.get(software.value));
            }
        }

        try {
            save();
        } catch (IOException e) {
            Log.e(TAG, "Error while writing package repository file "+e.getMessage());
        }
    }

    static void save() throws IOException {
        new File(UPDATE_PACKAGE_PATH).mkdirs();
        var title = Arrays.stream(DataType.values())
                .map(e->e.toString())
                .collect(Collectors.joining(","));
        var value = Arrays.stream(DataType.values())
                .map(e->e.value)
                .map(k->data.getOrDefault(k, ""))
                .collect(Collectors.joining(","));
        var bw = new BufferedWriter(new FileWriter(UPDATE_PACKAGE_PATH+"/"+SAVE_PATH));
        bw.write(title);
        bw.newLine();
        bw.write(value);
        bw.close();
    }


}
