package com.example.jetpackcomposeexample.controller.bus.connection;

import android.util.Log;

import com.example.jetpackcomposeexample.controller.bus.ResultCallback;
import com.example.jetpackcomposeexample.utils.TLog;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.util.Arrays;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
enum Status {
    PREPARING,
    WAITING,
    CONNECTING,
    PARSING,
    SENDING,
    FINISH,
    UNKNOWN
}
public class CommServer {
    public static Status status = Status.UNKNOWN;
    static final String TAG = CommServer.class.getSimpleName();
    static ResultCallback callback;
    static final ExecutorService executorService = Executors.newSingleThreadExecutor();

    public static Future<Boolean> start() {
        TLog.d("BUS_DATA", "Bus Comm is starting....");
        status = Status.PREPARING;
        return executorService.submit(runnable);
    }
    static final Callable<Boolean> runnable = () -> {
        try {
            callback = new ResultCallback();
            ServerSocket serverSocket;
            Socket socket;
            serverSocket = new ServerSocket(53001);
            serverSocket.setSoTimeout(10000);
            serverSocket.setReceiveBufferSize(13000);
            while (true) {
                try {
                    TLog.d(TAG, "Bus Comm is waiting for connecting....");
                    status = Status.WAITING;
                    socket = serverSocket.accept();
                    TLog.d(TAG, "Bus Comm is connecting....");
                    final int BUFFER_SIZE = 1024;
                    byte[] buffers = new byte[BUFFER_SIZE];
                    int size = 0;
                    while(socket.getInputStream().available() > 0) {
                        size = socket.getInputStream().read(buffers);
                        TLog.d(TAG, "socket read: "+ Arrays.toString(buffers));
                    }
                    byte[] rawData = Arrays.copyOfRange(buffers, 0, size);
                    TLog.d(TAG, "Read raw data successfully: "+Arrays.toString(rawData));
                    byte[] response = callback.parse(rawData);
                    socket.getOutputStream().write(response);
                    status = Status.SENDING;
                    socket.getOutputStream().flush();
                    TLog.d(TAG, "send response: "+Arrays.toString(response));
                    status = Status.FINISH;
                } catch (SocketTimeoutException e) {
                    //TODO: Add Error
                    TLog.d(TAG, "SocketTimeoutException");
                }
            }
        } catch (IOException e) {
            TLog.d(TAG, "IOException");
            return false;
        }
    };

}
