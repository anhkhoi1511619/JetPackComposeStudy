package com.example.jetpackcomposeexample.controller.tcp_ip_v2;

import android.util.Log;

import com.example.jetpackcomposeexample.utils.TLog_Sync;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class DataProcessor {
   static boolean shouldRun = true;
   static int port = 51002;
   static String command = "01";
   static String data = "";
   static boolean isCritical = false;
   static int retryCount;
   final static int RETRY_MAX = 5;
   public static void start() {
       new Thread(() -> {
           try {
               killProcessesByPort(port);
               while (shouldRun)
               {
                   CommPackageDTO dto = setData();
                   SendManager.sendAsync (dto, "192.168.204.111", port, isCritical);
                   try {
                       Thread.sleep (100);
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
               }
           } catch (Exception e){
               Log.d("DataProcessor","Exception: "+e);
           }
       }).start();
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
       shouldRun = false;
       killProcessesByPort(port);
    }
    private static void killProcessesByPort(int port) throws IOException {
        Process process;
        process = Runtime.getRuntime().exec("lsof -i :" + port);
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
            String line;
            while ((line = reader.readLine()) != null) {
                System.out.println("Found: " + line); // Debugging

                String[] parts = line.trim().split("\\s+");
                int pid = -1;
                try {
                    pid = Integer.parseInt(parts[1]); // Linux/macOS: PID ở cột thứ 2
                } catch (NumberFormatException ignored) {
                }

                if (pid > 0) {
                    killProcess(pid);
                }
            }
        }
    }

    private static void killProcess(int pid) {
        try {
            TLog_Sync.d("DataProcessor", "Killing process PID: " + pid);
            Runtime.getRuntime().exec("kill -9 " + pid);
        } catch (IOException e) {
            TLog_Sync.e("DataProcessor","Failed to kill PID " + pid + ": " + e.getMessage());
        }
    }
}
