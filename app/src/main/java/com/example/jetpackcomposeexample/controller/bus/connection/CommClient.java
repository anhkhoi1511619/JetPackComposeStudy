package com.example.jetpackcomposeexample.controller.bus.connection;

import android.util.Log;

import androidx.core.util.Consumer;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.util.Arrays;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class CommClient {
    Consumer<byte[]> callback;
    static final ExecutorService executorService = Executors.newSingleThreadExecutor();

    public static void start() {
        Log.d("BUS_DATA", "Bus Comm is starting....");
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
                    Log.d("BUS_DATA", "Bus Comm is waiting for connecting....");
                    socket = serverSocket.accept();
                    Log.d("BUS_DATA", "Bus Comm is connecting....");
                    BufferedInputStream fin = new BufferedInputStream(socket.getInputStream());
                    BufferedOutputStream count = new BufferedOutputStream(socket.getOutputStream());
                    int x;
                    final int UPLOAD_CHUNK_SIZE = 1024;
                    byte[] bytes = new byte[UPLOAD_CHUNK_SIZE];
                    while ((x = fin.read(bytes, 0, bytes.length)) > 0) {
                        Log.d("BUS_DATA", Arrays.toString(bytes) + "Length: "+x);
                    }
                    count.write(1);
                    fin.close();
                    count.flush();
                    count.close();
                } catch (SocketTimeoutException e) {
                    Log.d("BUS_DATA", "SocketTimeoutException");
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    };

}
