package com.example.jetpackcomposeexample.utils;

import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class TLog_Sync {
    static CsvLogger logger = new CsvLogger("app_log.csv", new String[]{
            "記録日時",
            "ログ区分",
            "ログデータ"
    });
    final static ScheduledThreadPoolExecutor executor =
            (ScheduledThreadPoolExecutor) Executors.newScheduledThreadPool(1);
    static  ScheduledFuture<?> scheduledTask;
    static SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
    final static String DELIMITER = "_EOF_";
    static StringBuilder stringBuilder = new StringBuilder();
    static boolean paused = false;
    static int error_count = 0;
    static void resetTmpString() {
        String[] tmp = stringBuilder.toString().split(DELIMITER);
        for (String line : tmp) {
            if (!line.isEmpty()) {
                logger.pushString(line.replace("\r\n", "").replace("\n", ""));
            } else {
                System.out.println("Empty");
            }
        }
        resetTask(); // Reset the task to start logging again
        stringBuilder.setLength(0);
    }
    // Bắt đầu chạy lặp lại mỗi 1 giây
    public static void startTask() {
        scheduledTask = executor.scheduleAtFixedRate(() -> {
            System.out.println("Task running ");
                resetTmpString();
        }, 1, 1, TimeUnit.MINUTES);
    }
    // Hủy task cũ và tạo lại
    public static void resetTask() {
        System.out.println("Resetting task...");
        if (scheduledTask != null && !scheduledTask.isCancelled()) {
            scheduledTask.cancel(false); // Hủy nhưng không interrupt nếu đang chạy
        }
        startTask(); // Bắt đầu lại task mới
    }

    public static void shutdown() {
        executor.shutdown();
    }
    public static int getStringBuilderLength() {
        return stringBuilder.toString().getBytes(StandardCharsets.UTF_8).length;
    }
    public static boolean isMaxTempString() {
        // return stringBuilder.length() >= (Integer.MAX_VALUE - 5000); // 1MB
        // return stringBuilder.length() >= 1100000000; // 1MB
        return stringBuilder.length() >= 1000 *1000 * 1024 /2; // 1MB

    }
    public static void pause() {
        paused = true;
    }
    public static void resume() {
        paused = false;
    }
    static void save(String tag, String content) throws RuntimeException
    {
        if(!paused) {
            logger.pushRawString(Arrays.asList(formatter.format(new Date()).toString(),tag, "\""+content.replace('"', '_')+"\""));
            pause();
        } else {
            saveTemp(tag, content);
        }
    }
    static void saveTemp(String tag, String content) throws RuntimeException
    {
        // if(isMaxTempString()) {
        //     throw new RuntimeException("Temporary string exceeded maximum length. Please reset the temporary string.");
        // }
        List<String> lineList = Arrays.asList(formatter.format(new Date()),tag, "\""+content.replace('"', '_')+"\"");
        String line = String.join(",", lineList);
        stringBuilder.append(line).append(DELIMITER);
    }
    synchronized static void logToFileNow(String tag, String content) {
        resume();
        resetTmpString();
    }

    public static void eLogToFileNow(String tag, String content) {
        logToFileNow(tag, content);
        e(tag, content);
    }
    public static void e(String content) {
        e("LAQRA", content);
    }
    public static void e(String tag, String content) {
       save(tag, content);
       System.out.println( content);
    }
    public static void e_40times(String tag, String content) {
        if(!paused && error_count++ == 40){
            error_count = 0;
            logger.pushRawString(Arrays.asList(formatter.format(new Date()).toString(),tag, "\""+content.replace('"', '_')+"\""));
            pause();
        } else if (error_count++ >= 40){
            error_count = 0;
            saveTemp(tag, content);
        }
        System.out.println( content);
    }

    public static void wLogToFileNow(String tag, String content) {
        logToFileNow(tag, content);
        w(tag, content);
    }
    public static void w(String content) {
        w("LAQRA", content);
    }
    public static void w(String tag, String content) {
        save(tag, content);
        System.out.println( content);
    }

    public static void dLogToFileNow(String tag, String content) {
        logToFileNow(tag, content);
        d(tag, content);
    }
    public static void d(String content) {
        d("LAQRA", content);
    }
    public static void d(String tag, String content) throws RuntimeException
    {
        save(tag, content);
        System.out.println( content);
    }
    public static void d(String tag, String content, boolean condition) {
        if(!paused && condition) {
            logger.pushRawString(Arrays.asList(formatter.format(new Date()).toString(),tag, "\""+content.replace('"', '_')+"\""));
            pause();
        } else if (condition) {
            saveTemp(tag, content);
        }
        System.out.println( content);
    }

    public static void iLogToFileNow(String tag, String content) {
        logToFileNow(tag, content);
        i(tag, content);
    }
    public static void i(String content) {
        i("LAQRA", content);
    }
    public static void i(String tag, String content) {
        save(tag, content);
        System.out.println(content);
    }

    public static void vLogToFileNow(String tag, String content) {
        logToFileNow(tag, content);
        v(tag, content);
    }
    public static void v(String content) {
        v("LAQRA", content);
    }
    public static void v(String tag, String content) {
        save(tag, content);
        System.out.println( content);
    }
}
