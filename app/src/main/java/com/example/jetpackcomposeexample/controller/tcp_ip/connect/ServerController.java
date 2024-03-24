package com.example.jetpackcomposeexample.controller.tcp_ip.connect;

import android.util.Log;


import com.example.jetpackcomposeexample.controller.tcp_ip.SocketEnum.DataStatus;
import com.example.jetpackcomposeexample.utils.CommLog;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.util.Arrays;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
public class ServerController {
    static final String TAG_SERVER = "CONNECT_SERVER_SUB";

    public ServerController(int port) {
        this.port = port;
        Log.d(TAG_SERVER, "ServerController will be created with port: "+this.port);
        create();
    }

    public void doClose() {
        close();
    }
    //------------------ This is library, please contact to Email:khoi.nguyen.ts.lecip.jp
    // if having bug or want to maintenance---------------------//
    static final ExecutorService executorServiceForSocketServer = Executors.newSingleThreadExecutor();
    //------------------ ↓↓↓Here will create socket and server socket↓↓↓---------------------//
    ServerSocket serverSocket;
    Socket socket;
    int port;
    void create() {
        executorServiceForSocketServer.submit(() -> {
            try {
                close();
                serverSocket = new ServerSocket(port);
                serverSocket.setSoTimeout(10);
                serverSocket.setReceiveBufferSize(13000);
                Log.d(TAG_SERVER, "Server is preparing to communication with port " + port);
            } catch (IOException e) {
                Log.e(TAG_SERVER, "Server is having error.... with port " + port + "detail error "+ e);
                e.printStackTrace();
            }
        });
    }
    DataStatus dataStatus;
    byte[] receive() {
        boolean isHandShake = false;
        byte[] emptyData = new byte[]{0};
        if(serverSocket == null) return emptyData;
        dataStatus = DataStatus.UNKNOWN;
        try {
            final int BUFFER_SIZE = 1024;
            byte[] buffer = new byte[BUFFER_SIZE];
            int size = 0;
            CommLog.d(TAG_SERVER, "Server is waiting for connecting with port"+port);
            if(socket != null && socket.getInputStream().available() > 0) {
                CommLog.d(TAG_SERVER, "Server Input stream of socket is available ");
                isHandShake = true;
            }
            if(!isHandShake)socket = serverSocket.accept();
            dataStatus = DataStatus.RECEIVING;
            CommLog.d(TAG_SERVER, "Server is connecting.... with port " + port);
            while (socket.getInputStream().available() > 0) {
                size = socket.getInputStream().read(buffer);
            }
            byte[] rawData = Arrays.copyOfRange(buffer, 0, size);
            CommLog.d(TAG_SERVER, "Server is read.... with data " + rawData.length);
            dataStatus = (rawData.length > 0) ? DataStatus.RECEIVE_DONE : DataStatus.RECEIVING;
            return rawData;
        } catch (SocketTimeoutException exception) {
            Log.e(TAG_SERVER, "Server is having error.... port + "+port+"with detail error "+exception);
        } catch (IOException e) {
            Log.e(TAG_SERVER, "Server is having error related in IO Stream.... port + "+port+"with detail error "+e);
        } catch (Exception e) {
            Log.e(TAG_SERVER, "Server is having unknown error.... port + "+port+"with detail error "+e);
        }
        dataStatus = DataStatus.UNKNOWN;
        return emptyData;
    }
    void close() {
        try {
            if(serverSocket != null) {
                serverSocket.close();
                serverSocket = null;
            }
            if(socket != null) {
                socket.getOutputStream().flush();
                socket.getOutputStream().close();
                socket.getInputStream().close();
                socket.close();
                socket = null;
            }
        }catch (IOException e) {
            Log.e(TAG_SERVER, "Socket or Socket Server can not close due to  "+ e);
        }
    }

}
