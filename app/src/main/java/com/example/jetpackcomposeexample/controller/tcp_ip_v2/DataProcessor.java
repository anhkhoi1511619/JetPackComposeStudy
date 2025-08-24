package com.example.jetpackcomposeexample.controller.tcp_ip_v2;

import android.util.Log;

import com.example.jetpackcomposeexample.utils.TLog_Sync;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Consumer;

public class DataProcessor {
    static int port = 51002;
    static String IP = "";
    static String command = "01";
    static String data = "";
    static boolean isCritical = false;
    static int retryCount;
    final static int RETRY_MAX = 5;
    static boolean isSendOK = false;
    final static ScheduledThreadPoolExecutor executor =
            (ScheduledThreadPoolExecutor) Executors.newScheduledThreadPool(1);
    static ScheduledFuture<?> scheduledTask;
    public static void start(String IP, int port, Consumer<Boolean> callback) {
        DataProcessor.IP = IP;
        DataProcessor.port = port;
        callback.accept(false);
        scheduledTask = executor.scheduleAtFixedRate(() ->  {
            try {
                CommPackageDTO dto = setData();
                Future<Boolean> result = SendManager.sendAsync (dto, IP, port, isCritical);
                if(isSendOK != result.get()){
                    callback.accept(result.get());
                    isSendOK = result.get();
                    TLog_Sync.d("DataProcessor", (isSendOK ? "Enable" : "Disable") + " Transits Feature" );
                }
                if (!command.equals("01")) {
                    retryCount++;
                    TLog_Sync.d("DataProcessor", "Receive command "+ command+" request retry "+retryCount+"/5");
                    if(retryCount > RETRY_MAX) {
                        retryCount = 0;
                        command = "01";
                        data = "";
                        isCritical = false;
                        TLog_Sync.dLogToFileNow("DataProcessor", "Reset command to 01 retry 5/5");
                    }
                }
            } catch (Exception e)
            {
                Log.d("DataProcessor","Exception: "+e);
            }
        }, 0, 100, TimeUnit.MILLISECONDS);
    }
    public static void setCommand(String cmd, String data){
        DataProcessor.command = cmd;
        DataProcessor.data = data;
        isCritical = true;
    }
    public static CommPackageDTO setData()
    {
        CommPackageDTO dto = new CommPackageDTO("02","03",0);
        dto.setTxSqNo("02");
        dto.setTxCmd(command);
        dto.setTxData(data);
        return dto;
    }
    public static void close() throws IOException {
        if (scheduledTask != null && !scheduledTask.isCancelled()) {
            scheduledTask.cancel(true);
        }
        ConnectionManager.getInstance().close(IP);
    }
}
