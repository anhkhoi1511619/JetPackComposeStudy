package com.example.jetpackcomposeexample.controller.tcp_ip.connect;

import android.util.Log;


import com.example.jetpackcomposeexample.controller.tcp_ip.SocketEnum.ConnectionStatus;
import com.example.jetpackcomposeexample.controller.tcp_ip.SocketEnum.DataStatus;

import java.io.IOException;
import java.net.Socket;
import java.util.Arrays;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ClientController {

    static final String TAG_CLIENT = "CONNECT_CLIENT";

    public ClientController(String IP, int port) {
        //Constructor
        this.IP = IP;
        this.port = port;
        Log.d(TAG_CLIENT, "ControllerInfo is created with IP: "+this.IP+ "port + "+this.port);
        openSocket();
    }

    public void doClose() {
        close();
    }

    public void doSend() {
        send();
    }
    public void setData(byte[] data) {
        this.data = data;
    }

    //------------------ This is library, please contact to Email:khoi.nguyen.ts.lecip.jp
    // if having bug or want to maintenance---------------------//
    static final ExecutorService executorServiceForSocket = Executors.newSingleThreadExecutor();
    byte[] data = new byte[] {1,2,3,4,5};

    //------------------ ↓↓↓Here will create socket↓↓↓---------------------//
    Socket socket;
    String IP;
    int port;

    public String getIP() {
        return IP;
    }

    ConnectionStatus connectionStatus;
    void openSocket() {
        Log.d(TAG_CLIENT, IP + "is preparing to creating....");
        executorServiceForSocket.submit(()->{
            close();
            try {
                connectionStatus = ConnectionStatus.CONNECTING;
                socket = new Socket(IP, port);
                Log.d(TAG_CLIENT, "Socket opened "+ socket.getRemoteSocketAddress());
                connectionStatus = ConnectionStatus.CONNECTED;
            } catch (IOException e) {
                Log.e(TAG_CLIENT, "Error while opening socket: "+e+"  at "+IP);
                connectionStatus = ConnectionStatus.CONNECT_FAIL;
            }
        });
    }
    DataStatus dataStatus;
    int reconnectCounter = 0;
    void send() {
        if(socket == null){
            Log.e(TAG_CLIENT, "Socket is not exists at "+IP);
            if(reconnectCounter != 5) {//Reset connect socket in 10s
                reconnectCounter++;
                connectionStatus = ConnectionStatus.RESET_CONNECT;
                openSocket();
                Log.e(TAG_CLIENT, "Socket is restart at "+IP);
                dataStatus = DataStatus.UNKNOWN;
            }
            return;
        }
        if(socket.isClosed()) {
            socket = null;//current socket is having error.Remove strictly now
            Log.e(TAG_CLIENT, "Socket is closing at"+IP+" Please wait for next step..");
            return;
        }
        dataStatus = DataStatus.SENDING;
        try {
            socket.getOutputStream().write(data);
            socket.getOutputStream().flush();
            Log.d(TAG_CLIENT, "Socket write "+Arrays.toString(data)+"  to address "+socket.getRemoteSocketAddress());
            dataStatus = DataStatus.SEND_DONE;
        } catch (IOException e) {
            Log.e(TAG_CLIENT, "Error while write data of socket "+socket.getRemoteSocketAddress()+" at "+IP);
            Log.e(TAG_CLIENT, "Socket is restart at "+IP);
            openSocket();
            dataStatus = DataStatus.UNKNOWN;
        }
    }
    //------------------ ↓↓↓Here will close socket↓↓↓---------------------//
    void close() {
        if(socket == null) {
            return;
        }
        try {
            socket.getInputStream().close();
            socket.getOutputStream().flush();
            socket.getOutputStream().close();
            socket.close();
            socket = null;
            connectionStatus = ConnectionStatus.CLOSED;
        } catch (IOException e) {
            Log.e(TAG_CLIENT, "Error while closing socket "+socket.getRemoteSocketAddress()+" : "+e);
        }
    }
}
