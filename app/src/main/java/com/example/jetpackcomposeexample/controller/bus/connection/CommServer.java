package com.example.jetpackcomposeexample.controller.bus.connection;

import android.util.Log;

import androidx.core.util.Consumer;

import com.example.jetpackcomposeexample.utils.TLog;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.util.Arrays;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class CommServer {
    Consumer<byte[]> callback;
    static final ExecutorService executorService = Executors.newSingleThreadExecutor();

    public static void start() {
        TLog.d("BUS_DATA", "Bus Comm is starting....");
        executorService.submit(runnable);
    }
     static final Runnable runnable = () -> {
        try {
            ServerSocket serverSocket;
            Socket socket;
            serverSocket = new ServerSocket(53001);
            serverSocket.setSoTimeout(10000);
            serverSocket.setReceiveBufferSize(13000);
            while (true) {
                try {
                    TLog.d("BUS_DATA", "Bus Comm is waiting for connecting....");
                    socket = serverSocket.accept();
                    TLog.d("BUS_DATA", "Bus Comm is connecting....");
                    final int BUFFER_SIZE = 1024;
                    while(socket.getInputStream().available() > 0) {
                        byte[] buffers = new byte[BUFFER_SIZE];
                        socket.getInputStream().read(buffers);                    //TODO: Add callback
                        TLog.d("BUS_DATA", "socket read: "+ Arrays.toString(buffers));
                    }
                    TLog.d("BUS_DATA", "Read raw data successfully");
                    socket.getOutputStream().write(new byte[]{1,2});//TODO: Add DTO data
                    socket.getOutputStream().flush();
                } catch (SocketTimeoutException e) {
                    //TODO: Add Error
                    TLog.d("BUS_DATA", "SocketTimeoutException");
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    };

}
