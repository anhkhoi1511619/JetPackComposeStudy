package com.example.jetpackcomposeexample.utils;

import android.util.Log;

import java.text.DateFormat;
import java.util.Arrays;
import java.util.List;
//USELESS
public class TLog {
    static CsvLogger logger = new CsvLogger("app_log.csv", new String[] {
            "記録日時",
            "ログ区分",
            "ログデータ"
    });
    public static void d(String tag, String content) {
        List<String> data = Arrays.asList(String.valueOf(DateFormat.getInstance().format(System.currentTimeMillis())),
                tag,
                "\""+content+"\"");
        logger.pushRawString(data);
        Log.d(tag, content);
    }
}
