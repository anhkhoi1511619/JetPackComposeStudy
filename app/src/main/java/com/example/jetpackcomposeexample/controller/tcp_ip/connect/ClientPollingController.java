package com.example.jetpackcomposeexample.controller.tcp_ip.connect;

import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;
import android.util.Log;

import androidx.annotation.RequiresApi;

import com.example.jetpackcomposeexample.controller.tcp_ip.SocketEnum.ConnectionStatus;
import com.example.jetpackcomposeexample.controller.tcp_ip.SocketEnum.DataStatus;
import com.example.jetpackcomposeexample.controller.tcp_ip.SocketEnum.IPInfo;
import com.example.jetpackcomposeexample.controller.tcp_ip.SocketEnum.PortInfo;
import com.example.jetpackcomposeexample.model.train.dto.TrainCommPackageDTO;
import com.example.jetpackcomposeexample.model.train.factory.SendFactory;
import com.example.jetpackcomposeexample.utils.CommLog;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ClientPollingController {
    public ClientPollingController(String name, List<ClientController> clientControllerList) {
        this.name = name;
        this.clientControllerList = clientControllerList;
    }

    static final String TAG_POLLING = "POLLING_CLIENT";
    HandlerThread clientSocketHandlerThread;
    Handler clientSocketHandler;

    public Handler getClientSocketHandler() {
        return clientSocketHandler;
    }

    String name;
    List<ClientController> clientControllerList;

    public List<ClientController> getClientControllerList() {
        return clientControllerList;
    }

    Message newMessage = new Message();
    PingChecker pingChecker;
    @RequiresApi(api = Build.VERSION_CODES.N)
    public void start() {
        clientSocketHandlerThread = new HandlerThread(name);
        clientSocketHandlerThread.start();
        clientSocketHandler = new Handler(clientSocketHandlerThread.getLooper()) {
            @Override
            public void handleMessage(Message msg){
                super.handleMessage(msg);
                Log.e(TAG_POLLING, "Receive message "+msg.what+"from user");
                newMessage = msg;
            }
        };
        pingChecker = new PingChecker(clientControllerList);
        pingChecker.check();
        initSequenceList();
        polling();
    }
    public void sendMessage(int command) {
        if(clientSocketHandler == null) return;
        Message msg = Message.obtain();
        msg.what = command;
        newMessage = msg;
        clientSocketHandler.handleMessage(msg);
    }
    public void sendMessage(int command, boolean isMainCtr, Bundle objects) {
        if(clientSocketHandler == null) return;
        Message msg = Message.obtain();
        msg.what = command;
        msg.obj = objects;
        msg.arg2 = isMainCtr ? 1 : 0;
        clientSocketHandler.handleMessage(msg);
    }

    ServerPollingController servers;
    public void setRelationShip(ServerPollingController servers) {
        this.servers = servers;
    }
    HashMap<ClientController, DataStatus> statusMap = new HashMap<>();
    public boolean isSendDone() {
        if(statusMap == null) return false;
        return !(statusMap.containsValue(DataStatus.SENDING)) && !statusMap.containsValue(DataStatus.UNKNOWN);
    }
    //------------------ This is library, please contact to Email:khoi.nguyen.ts.lecip.jp
    // if having bug or want to maintenance---------------------//

    void polling() {
        clientSocketHandler.post(runnable);
    }
    final long POLLING_PERIOD = 100L;
    final Runnable runnable = new Runnable() {
        @RequiresApi(api = Build.VERSION_CODES.N)
        @Override
        public void run() {
            Log.d(TAG_POLLING, "Run after 100ms");
            CommLog.d(TAG_POLLING, "Polling is running and data status is SENDING with counter: "+ counter);
            for (ClientController client : clientControllerList) {
                if(client != null)  {
                    if (shouldReconnect(client)) client.reconnectCounter = 0;//Client will reset connect after losing connect
                    Log.d(TAG_POLLING, "ClientController have IP "+client.IP+
                            " reachable: "+pingChecker.isReachable(client));
                    if(pingChecker.isReachable(client)) {//If Host is reachable then send data
                        byte[] data = packageData(client);
                        client.setData(data);
                        client.doSend();
                        statusMap.put(client, client.dataStatus);
                        increaseSequence(client);
                        CommLog.d(TAG_POLLING, "Polling is sending client: "+ client.IP+"   with status: "+statusMap.get(client));
                    }
                }
            }
            CommLog.d(TAG_POLLING, "Polling is finished with all status "+(isSendDone() ? "TRUE" : "FALSE")+" after send to all of client and counter "+counter);
            checkCounter();
            counter++;
            clientSocketHandler.postDelayed( this, POLLING_PERIOD);
        }
    };


    SendFactory sendFactory;

    public void setSendFactory(SendFactory sendFactory) {
        this.sendFactory = sendFactory;
    }

    //DTO
    final int STATUS_REQUEST_MAIN_COMMAND = 0;
    final int STATUS_REQUEST_SUB_COMMAND = 1;
    public byte[] packageData(ClientController clientController) {//TODO: global function (public) for JUnit Test. After test done, please change to local (private)
        boolean isMainCtrlPackage = IPInfo.fromIP(clientController.IP).key == 1;
        int serverPort = PortInfo.findSameKey(clientController.port);
        int command = isMainCtrlPackage ? STATUS_REQUEST_SUB_COMMAND : STATUS_REQUEST_MAIN_COMMAND;
        try {
            TrainCommPackageDTO serverData = servers.listData.get(serverPort);
            int sequenceServer = servers.sequenceData.get(serverPort);
            int sequence = (sequenceMap.isEmpty()) ? 0 : sequenceMap.get(clientController);
            if(newMessage.what != 0) {//Choose command by user
                command = newMessage.what;
                Log.d(TAG_POLLING, "Choose command from user "+command);
            } else {//Choose command by data of server
                command = (serverData == null) ? command : (serverData.getCommand()+1);
                Log.d(TAG_POLLING, "Choose command from data of server "+((serverData==null) ? "default command" : command));
            }
            if(isMainCtrlPackage)  sequence = sequenceServer;//all the sequence of sub-controller is belong to the sequence of main - controller
            return sendFactory.fill(command, sequence, newMessage.obj).serialize();
        } catch (Exception e) {//Try 1 times to send
            Log.e(TAG_POLLING, "Exception is occurred when parse DTO . Null value or other.."+e);
            //TODO: This is unnecessary. I know But still try hard to catch exception
            try {//Try 1 times to send
                return sendFactory.fill(command, 0, newMessage.obj).serialize();
            } catch (Exception exception){
                return new byte[]{0};// Try hard fail
            }
        }
    }
    Map<ClientController, Integer> sequenceMap = new HashMap<>();

    public void setSequenceMap(Map<ClientController, Integer> sequenceMap) {//Create for JUnit
        this.sequenceMap = sequenceMap;
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    void initSequenceList() {
        if(sequenceMap!=null) sequenceMap.clear();
        clientControllerList.forEach(a->{
            sequenceMap.put(a,0);
        });
    }
    @RequiresApi(api = Build.VERSION_CODES.N)
    void increaseSequence(ClientController clientController){
        if(sequenceMap.isEmpty()) return;
        if((clientController.dataStatus == DataStatus.SEND_DONE) &&
                (clientController.connectionStatus == ConnectionStatus.CONNECTED))
            sequenceMap.merge(clientController, 1, Integer::sum);
        CommLog.d(TAG_POLLING, "Sequence of "+clientController.IP +" is "+sequenceMap.get(clientController));
    }
    long counter = 0;
    void checkCounter() {
        if(servers == null) return;
        if(counter!= servers.counter) counter = servers.counter;
    }
    @RequiresApi(api = Build.VERSION_CODES.N)
    boolean shouldReconnect(ClientController clientController) {
        if(clientController.reconnectCounter == 0) return false;

        for (ServerController server : servers.serverControllerList) {
            CommLog.d(TAG_POLLING, "Friend is having port " + server.port + " with data status: " + server.dataStatus + " with counter " + servers.counter);
            if(PortInfo.fromPort(clientController.port).key == PortInfo.fromPort(server.port).key) {
                if(server.dataStatus == DataStatus.RECEIVE_DONE ) {
                    CommLog.d(TAG_POLLING, "Main is listen data so reset connect");
                    return true;
                }
            }
        }
        return false;
    }
}
