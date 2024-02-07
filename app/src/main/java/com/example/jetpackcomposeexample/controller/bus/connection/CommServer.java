package com.example.jetpackcomposeexample.controller.bus.connection;

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
                    BufferedInputStream fin = new BufferedInputStream(socket.getInputStream());
                    BufferedOutputStream count = new BufferedOutputStream(socket.getOutputStream());
                    int x;
                    final int UPLOAD_CHUNK_SIZE = 1024;
                    byte[] bytes = new byte[UPLOAD_CHUNK_SIZE];
                    while ((x = fin.read(bytes, 0, bytes.length)) > 0) {
                        TLog.d("BUS_DATA", Arrays.toString(bytes) + "Length: "+x);
                    }
                    count.write(new byte[]{1,2});
                    fin.close();
                    count.flush();
//                    count.close();
                } catch (SocketTimeoutException e) {
                    TLog.d("BUS_DATA", "SocketTimeoutException");
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    };

}
