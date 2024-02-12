package com.example.jetpackcomposeexample.controller.bus.connection;

import android.util.Log;

import androidx.core.util.Consumer;

import com.example.jetpackcomposeexample.controller.bus.ResultCallback;
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
    static ResultCallback callback;
    static final ExecutorService executorService = Executors.newSingleThreadExecutor();

    public static void start() {
        TLog.d("BUS_DATA", "Bus Comm is starting....");
        executorService.submit(runnable);
    }
     static final Runnable runnable = () -> {
        try {
            callback = new ResultCallback();
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
                    byte[] buffers = new byte[BUFFER_SIZE];
                    while(socket.getInputStream().available() > 0) {
                        socket.getInputStream().read(buffers);
                        TLog.d("BUS_DATA", "socket read: "+ Arrays.toString(buffers));
                    }
                    TLog.d("BUS_DATA", "Read raw data successfully");
                    byte[] response = callback.parse(buffers);
                    socket.getOutputStream().write(response);
                    socket.getOutputStream().flush();
                    TLog.d("BUS_DATA", "send response: "+Arrays.toString(response));
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
