package com.example.jetpackcomposeexample.controller.tcp_ip_v2;

import android.util.Log;

import com.example.jetpackcomposeexample.utils.DataTypeConverter;
import com.example.jetpackcomposeexample.utils.TLog_Sync;

import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Arrays;
import java.util.function.Consumer;
import java.net.InetAddress;
import java.util.concurrent.*;

public class RcvManager {
    TxManager txManager;
    final ExecutorService executor = Executors.newSingleThreadExecutor();
    ScheduledFuture<?> pingHandler;
    final int port;
    int count = 0;
    public static boolean shouldReconnect = false;
    ScheduledExecutorService scheduledTimer = Executors.newScheduledThreadPool(1);

    public RcvManager (int port, Consumer<byte[]> callback)
    {
        this.port = port;
        this.txManager = new TxManager();
        TLog_Sync.d("RcvManager", "[RX]RcvManager initialized for " + port);
//        startCheckStatus(port);
        startListening(callback);
    }
//    void startCheckStatus(int port) {
//        new PortMonitorCompact(port).startMonitoring();
//    }
    void startListening(Consumer<byte[]> callback) {
        shouldReconnect = false;
        executor.execute(() -> {
            TLog_Sync.d("RcvManager","[RX]Establishing reader communication thread for " + port);
            try {
                ServerSocket serverSocket = new ServerSocket(port);
                serverSocket.setReceiveBufferSize(13000);
                byte[] data = new byte[13000];
                // todo データ処理を呼ぶ
                while (!Thread.currentThread().isInterrupted()) {
                    TLog_Sync.d("RcvManager","[RX]Waiting for connection for " + ":" + port);
                    Socket socket = serverSocket.accept();
                    startPinging(socket.getInetAddress().getHostAddress());
                    TLog_Sync.d("RcvManager","[RX]Connection accepted from " + socket.getInetAddress() + ":" + port);
                    socket.setSoTimeout(100);
                    TLog_Sync.d("RcvManager","[RX]Server Socket opened for " + socket.getInetAddress() + ":" + port);
                    OutputStream os = socket.getOutputStream();
                    int size = 0;
                    int offset = 0;
                    while (!shouldReconnect && !Thread.currentThread().isInterrupted()) {
                        try {
                            Log.d("RcvManager","[RX]Server Socket read for " + socket.getInetAddress() + ":" + port + " count: " + count++);
                            if (socket.isClosed() || !socket.isConnected()) {
                                TLog_Sync.d("RcvManager","[RX]Socket is closed or not connected, breaking the loop.");
                                break;
                            }
                            while (socket.getInputStream().available() > 0) {
                                size = socket.getInputStream().read(data);
                            }
                            if (size <= 0) {
                                count = 0;
                                TLog_Sync.d("RcvManager","[RX]No data received, breaking the loop.");
                                break;
                            }

                            byte[] rawData = Arrays.copyOfRange(data, 0, size);
                            Log.d("RcvManager","[RX]Server is read.... with length " + rawData.length + " data: " + DataTypeConverter.format(rawData));

                            byte[] response = txManager.createCommand(rawData); // Example response data
                            // System.arraycopy(rawData, 0, response, 0, size);
                            os.write(response);
                            os.flush();
                            Log.d("RcvManager","[RX-RES]Server Socket write "+DataTypeConverter.format(response)+" for " + socket.getInetAddress() + ":" + port);
                            callback.accept(rawData);
                            Thread.sleep(100);
                        } catch (Exception e) {
                            TLog_Sync.d("RcvManager","[RX]Error while receiving data from reader-client side" + socket.getInetAddress() + ":" + port + " - " + e.getMessage());
                            break; // Break the loop on error
                        }
                    }
                    TLog_Sync.d("RcvManager","[RX]Closing socket for " + socket.getInetAddress() + ":" + port);
                    os.close();
                    socket.close();
                    shouldReconnect = false;
                }
            } catch (Exception e) {
                TLog_Sync.d("RcvManager","[RX]Error while receiving data from reader-client side" + port + " - " + e.getMessage());
            }
        });
    }

    void startPinging(String ip) {
        TLog_Sync.d("RcvManager","[RX]Starting pinging for " + ip );
        if (pingHandler != null && !pingHandler.isCancelled()) {
            pingHandler.cancel(true);
        }
        pingHandler = scheduledTimer.scheduleAtFixedRate(() -> {
            try {
                InetAddress inet = InetAddress.getByName(ip);
                Log.d("RcvManager","[RX]Pinging " + ip + " ...");
                if (inet.isReachable(1000)) { // timeout 5 giây
                    Log.d("RcvManager","[RX]"+ip + " is reachable");
                } else {
                    TLog_Sync.d("RcvManager","[RX]"+ip + " is NOT reachable");
                    shouldReconnect = true;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }, 0, 5, TimeUnit.SECONDS);
    }
}