package com.example.jetpackcomposeexample.controller.tcp_ip.connect;

import android.os.Build;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;
import android.util.Log;

import androidx.annotation.RequiresApi;

import com.example.jetpackcomposeexample.controller.tcp_ip.SocketEnum.DataStatus;
import com.example.jetpackcomposeexample.controller.train.CommandInfo;
import com.example.jetpackcomposeexample.model.train.dto.TrainCommPackageDTO;
import com.example.jetpackcomposeexample.utils.CommLog;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Consumer;
public class ServerPollingController {
    static final String TAG_POLLING = "POLLING_SERVER";
    public ServerPollingController(String name, List<ServerController> serverControllerList) {
        this.name = name;
        this.serverControllerList = serverControllerList;
    }
    HandlerThread socketHandlerThread;
    Handler socketHandler;
    HandlerThread receiveMessageHandlerThread;
    Handler receiveMessageHandler;
    String name;
    List<ServerController> serverControllerList;

    public List<ServerController> getServerControllerList() {
        return serverControllerList;
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public void start() {
        socketHandlerThread = new HandlerThread(name);
        socketHandlerThread.start();
        socketHandler = new Handler(socketHandlerThread.getLooper());
        //Purpose of Thread: Receive Message and Avoid heavy traffic in socketHandler
        receiveMessageHandlerThread = new HandlerThread(name+" Message");
        receiveMessageHandlerThread.start();
        receiveMessageHandler = new Handler(receiveMessageHandlerThread.getLooper()){
            @Override
            public void handleMessage(Message msg){
                super.handleMessage(msg);
                Log.e(TAG_POLLING, "Receive message "+msg.what+"from user. Please check");
                Log.e(TAG_POLLING, "Receive value "+msg.arg1+"from user. Please check");
                callback.accept(msg);
                isBusy = false;
                Log.e(TAG_POLLING, "Message is sent. Busy Value is cancel");
            }
        };
        counter = 0;
        polling();
    }
    Consumer<Message> callback;

    public void setCallback(Consumer<Message> callback) {
        this.callback = callback;
    }
    public void sendMessage(int port, TrainCommPackageDTO objects) {
        if(receiveMessageHandler == null) return;
        Message msg = Message.obtain();
        msg.what = port;
        msg.obj = objects;
        receiveMessageHandler.sendMessage(msg);
    }
    public void sendMessageAtFrontOfQueue(int port, TrainCommPackageDTO objects) {
        if(receiveMessageHandler == null) return;
        Message msg = Message.obtain();
        msg.what = port;
        msg.obj = objects;
        receiveMessageHandler.sendMessageAtFrontOfQueue(msg);
    }
    public boolean isReceiveDone() {
        if(statusMap == null) return false;
        return !statusMap.containsValue(DataStatus.RECEIVING) && !statusMap.containsValue(DataStatus.UNKNOWN);
    }

    //------------------ This is library, please contact to Email:khoi.nguyen.ts.lecip.jp
    // if having bug or want to maintenance---------------------//
    void polling() {
        socketHandler.post(runnable);
    }
    HashMap<Integer, TrainCommPackageDTO> listData = new HashMap<>();
    HashMap<Integer, Integer> sequenceData = new HashMap<>();
    public Map<Integer, TrainCommPackageDTO> getListData() {//For Junit Test
        return listData;
    }

    public HashMap<Integer, Integer> getSequenceData() {
        return sequenceData;
    }

    final long POLLING_PERIOD = 100L;
    final Runnable runnable = new Runnable() {
        @RequiresApi(api = Build.VERSION_CODES.N)
        @Override
        public void run() {
            CommLog.d(TAG_POLLING, "Run after 100ms");
            CommLog.d(TAG_POLLING, "Polling is running and data status is RECEIVING with counter: "+ counter);
            for (ServerController serverController : serverControllerList) {
                if(serverController!=null) {
                    byte[] data = serverController.receive();
                    TrainCommPackageDTO packageDTO = parsePackage(data);
                    callBack(serverController.port,packageDTO);
                    Log.d(TAG_POLLING, "Run after 100ms having data: "+ Arrays.toString(data));
                    statusMap.put(serverController, serverController.dataStatus);
                    sequenceData.put(serverController.port, packageDTO.getSequenceNum());
                    CommLog.d(TAG_POLLING, "Polling is receive from server: "+ serverController.port+"   with status: "+statusMap.get(serverController));
                }
            }
            CommLog.d(TAG_POLLING, "Polling is finished with all status "+(isReceiveDone() ? "TRUE" : "FALSE")+" after receive from all of server and counter "+counter);
            counter++;
            socketHandler.postDelayed( this, POLLING_PERIOD);
        }
    };
    boolean isBusy = false;
    @RequiresApi(api = Build.VERSION_CODES.N)
    public TrainCommPackageDTO parsePackage(byte[] data) {
        TrainCommPackageDTO dto = new TrainCommPackageDTO();
        try {
            dto.deserialize(data);
            return dto;
        } catch (Exception e) {
            Log.e(TAG_POLLING, "Error occurred when parsing package of server controller"+e);
        }
        return dto;
    }

    HashMap<ServerController, DataStatus> statusMap = new HashMap<>();
    long counter;
    public enum MessageType {
        UNKNOWN_MESSAGE,
        EXISTED_MESSAGE,
        NORMAL_MESSAGE,
        HIGH_PRIORITY_MESSAGE
    }
    @RequiresApi(api = Build.VERSION_CODES.N)
    void callBack(int port, TrainCommPackageDTO data) {
        MessageType type = getType(port, data);
        Log.d(TAG_POLLING, "Message type is "+type+" at port "+port);
        switch (type){
            case HIGH_PRIORITY_MESSAGE:
                isBusy = true;
                sendMessageAtFrontOfQueue(port, data);
                break;
            case NORMAL_MESSAGE:
                sendMessage(port, data);
                break;
            default:
                break;
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public MessageType getType(int port, TrainCommPackageDTO data) {
        if(isBusy) {
            Log.e(TAG_POLLING, "High Priority Message is received so all low priority will be reject with port "
                    +port +" command "+data.getCommand() + " data section: "+ Arrays.toString(data.getData()));
            return MessageType.UNKNOWN_MESSAGE;
        }
        if(!listData.containsKey(port)) {
            Log.d(TAG_POLLING, "listData does not contains port "+port);
            setListData(port, data);
            return MessageType.UNKNOWN_MESSAGE;
        }
        TrainCommPackageDTO previousData = listData.get(port);
        if((previousData.getCommand() == data.getCommand()) &&
                Arrays.equals(previousData.getData(), data.getData())) {
            Log.d(TAG_POLLING, "Data is exists in list data");
            return MessageType.EXISTED_MESSAGE;
        }
        if(data.isCorrectData()) {
            setListData(port, data);
            boolean isHighPriorityCommand =  CommandInfo.isHighPriority(data.getCommand());
            Log.e(TAG_POLLING, "Message is sending........ Please waiting to sent done");
            Log.e(TAG_POLLING, "Command of dto: "+data.getCommand());
            Log.e(TAG_POLLING, "Data of dto: "+Arrays.toString(data.getData()));
            Log.e(TAG_POLLING, "Port is changed :"+port);
            Log.e(TAG_POLLING, "Data of dto should send");
            Log.e(TAG_POLLING, "isBusy is "+isBusy);
            if(isHighPriorityCommand) {
                return MessageType.HIGH_PRIORITY_MESSAGE;
            }
            return MessageType.NORMAL_MESSAGE;
        }
        return MessageType.UNKNOWN_MESSAGE;
    }
    @RequiresApi(api = Build.VERSION_CODES.N)
    void setListData(int port, TrainCommPackageDTO data) {
        Log.d(TAG_POLLING, " list data will be set with port "+port+" command "+data.getCommand()+ " data section: "+ Arrays.toString(data.getData()));
        listData.put(port, data);
        listData.keySet().removeIf(Objects::isNull);
    }
}

