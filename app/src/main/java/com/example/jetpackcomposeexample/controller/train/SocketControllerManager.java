package com.example.jetpackcomposeexample.controller.train;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Message;
import android.util.Log;

import androidx.annotation.RequiresApi;

import com.example.jetpackcomposeexample.controller.tcp_ip.SocketEnum.IPInfo;
import com.example.jetpackcomposeexample.controller.tcp_ip.SocketEnum.PortInfo;
import com.example.jetpackcomposeexample.controller.tcp_ip.connect.ClientPollingController;
import com.example.jetpackcomposeexample.controller.tcp_ip.connect.ServerPollingController;
import com.example.jetpackcomposeexample.model.train.TrainTemp;
import com.example.jetpackcomposeexample.model.train.dto.MainDirectionRequest;
import com.example.jetpackcomposeexample.model.train.dto.RouteRequest;
import com.example.jetpackcomposeexample.model.train.dto.StopStationRequest;
import com.example.jetpackcomposeexample.model.train.dto.SubDirectionRequest;
import com.example.jetpackcomposeexample.model.train.dto.TrainCommPackageDTO;
import com.example.jetpackcomposeexample.controller.tcp_ip.connect.ClientController;
import com.example.jetpackcomposeexample.controller.tcp_ip.connect.ServerController;
import com.example.jetpackcomposeexample.model.train.factory.MainFactory;
import com.example.jetpackcomposeexample.model.train.factory.SubFactory;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Consumer;


public class SocketControllerManager {
    static SocketControllerManager instance;
    public static SocketControllerManager getInstance() {
        if(instance == null){
            //TODO: reset all communication
            instance = new SocketControllerManager();
        }
        return instance;
    }
    Context context;

    public void setContext(Context context) {
        this.context = context;
    }

    final String OPERATION_FRAGMENT_BROADCAST = "operation-action-local-broadcast";
    final String MAIN_ACTIVITY_BROADCAST = "main-activity-action-local-broadcast";

    String TAG = SocketControllerManager.class.getSimpleName();
    int[] controllerArr = new int[]{1,1,1,-1};

    public int[] getControllerArr() {
        return controllerArr;
    }

    int controllerID = 1;
    ClientPollingController clientPollingController;
    ServerPollingController serverPollingController;
    final int MASTER_KEY = 1;
    final Consumer<Message> messageConsumer = (message) -> {
        int port = message.what;
        TrainCommPackageDTO dto = (TrainCommPackageDTO) message.obj;
        int command = dto.getCommand();
        byte[] data = dto.getData();
        Log.d(TAG, "Handle message from background with port" + port);
        Log.d(TAG, "Handle message from background with command" + command);
        Log.d(TAG, "Handle message from background with data" + Arrays.toString(data));

        if(controllerID == MASTER_KEY) { //TODO: Add here for main controller
            switch (CommandInfo.get(command)) {
                case STATUS_SUB_RESPONSE:
                    //TODO: Receive command サブ制御部⇒メイン制御部 01h
                    Log.d(TAG, "Receive command サブ制御部⇒メイン制御部 01h");
                    break;
                case ROUTE_ID_SUB_RESPONSE:
                    //TODO: Receive command サブ制御部⇒メイン制御部 11h
                    Log.d(TAG, "Receive command サブ制御部⇒メイン制御部 11h");
                    break;
                case STOP_SEQ_SUB_RESPONSE:
                    //TODO: Receive command サブ制御部⇒メイン制御部 21h
                    Log.d(TAG, "Receive command サブ制御部⇒メイン制御部 21h");
                    break;
                case DIRECTION_SUB_RESPONSE:
                    //TODO: Receive command サブ制御部⇒メイン制御部 31h
                    Log.d(TAG, "Receive command サブ制御部⇒メイン制御部 31h");
                    break;
                case ROUTE_ID_SUB_REQUEST:
                    Log.d(TAG, "サブ制御部⇒メイン制御部 160h");
                    int currentRouteId = RouteRequest.getCurrentRouteId(dto);
                    TrainTemp.currentRouteId = currentRouteId;
                    send(MAIN_ACTIVITY_BROADCAST,command,currentRouteId, 0);
                    sendMessage(CommandInfo.CHANGE_ROUTE,true,currentRouteId);
                    break;
                case STOP_SEQ_SUB_REQUEST:
                    int currentStopSeq = StopStationRequest.getStopSeq(dto);
                    int currentOperation = StopStationRequest.getOperationNum(dto);
                    TrainTemp.currentStopSeq = currentStopSeq;
                    TrainTemp.operationNum = currentOperation;
                    send(MAIN_ACTIVITY_BROADCAST,command,currentStopSeq, currentOperation);
                    send(OPERATION_FRAGMENT_BROADCAST,command,currentStopSeq, currentOperation);
                    sendMessage(CommandInfo.CHANGE_STOP_SEQUENCE,true,currentStopSeq, currentOperation);
                    Log.d(TAG, "サブ制御部⇒メイン制御部 176h");
                    break;
                case DIRECTION_SUB_REQUEST:
                    //TODO: Receive command サブ制御部⇒メイン制御部 192h
                    Log.d(TAG, "サブ制御部⇒メイン制御部 192h");
                    SubDirectionRequest subDirectionRequest = new SubDirectionRequest();
                    subDirectionRequest.deserialize(data);
                    TrainTemp.directionNum = subDirectionRequest.direction;//TODO: Fix here
                    TrainTemp.controllerNumber = subDirectionRequest.controllerNumber;//TODO: Fix here
                    break;
            }

        } else {//TODO: Add here for sub controller
            switch (CommandInfo.get(command)) {
                case STATUS_MAIN_REQUEST:
                    //TODO: Receive command サブ制御部⇒メイン制御部 00h
                    Log.d(TAG, "Receive command メイン制御部⇒サブ制御部 00h");
                    break;
                case ROUTE_ID_MAIN_REQUEST:
                    Log.d(TAG, "Receive command メイン制御部⇒サブ制御部 10h");
                    TrainTemp.currentRouteId = RouteRequest.getCurrentRouteId(dto);
                    send(MAIN_ACTIVITY_BROADCAST,command,TrainTemp.currentRouteId, 0);
                    Log.d(TAG, "Receive route number: "+TrainTemp.currentRouteId);
                    break;
                case STOP_SEQ_MAIN_REQUEST:
                    int currentStopSeq = StopStationRequest.getStopSeq(dto);
                    int currentOperation = StopStationRequest.getOperationNum(dto);
                    TrainTemp.currentStopSeq = currentStopSeq;
                    TrainTemp.operationNum = currentOperation;
                    send(MAIN_ACTIVITY_BROADCAST,command,currentStopSeq, currentOperation);
                    send(OPERATION_FRAGMENT_BROADCAST,command,currentStopSeq, currentOperation);
                    Log.d(TAG, "Receive stop number: "+ currentStopSeq);
                    Log.d(TAG, "Receive operation number: "+ currentOperation);
                    break;
                case DIRECTION_MAIN_REQUEST:
                    Log.d(TAG, "Receive command メイン制御部⇒サブ制御部 30h");
                    MainDirectionRequest mainDirectionRequest = new MainDirectionRequest();
                    mainDirectionRequest.deserialize(data);
                    TrainTemp.directionNum = mainDirectionRequest.direction;
                    Log.d(TAG, "Receive direction number: "+mainDirectionRequest.direction);
                    break;
                case ROUTE_ID_MAIN_RESPONSE:
                    //TODO: Receive command サブ制御部⇒メイン制御部 161h
                    Log.d(TAG, "Receive command メイン制御部⇒サブ制御部 161h");
                    break;
                case STOP_SEQ_MAIN_RESPONSE:
                    //TODO: Receive command サブ制御部⇒メイン制御部 177h
                    Log.d(TAG, "Receive command メイン制御部⇒サブ制御部 177h");
                    break;
                case DIRECTION_MAIN_RESPONSE:
                    //TODO: Receive command サブ制御部⇒メイン制御部 193h
                    Log.d(TAG, "Receive command メイン制御部⇒サブ制御部 193h");
                    break;
            }
        }
    };

    /**
     * For FrontEnd
     * @param intentName
     * @param command
     * @param value1
     * @param value2
     */
    public void send(String intentName, int command, int value1, int value2) {
        Intent intent = new Intent(intentName);//For UI
        intent.putExtra("command", command);
        switch (CommandInfo.get(command)) {
            case ROUTE_ID_MAIN_REQUEST:
            case ROUTE_ID_SUB_REQUEST:
                intent.putExtra("messageRoute", value1);
                Log.d("BroadcastReceiver", " Send message is "+ value1);
                break;
            case STOP_SEQ_MAIN_REQUEST:
            case STOP_SEQ_SUB_REQUEST:
                intent.putExtra("messageStop", value1);
                intent.putExtra("messageOperation", value2);
                Log.d("BroadcastReceiver", " Send message with value1 "+ value1 +" value2" +value2);
                break;
        }
        // on below line we are sending our broad cast with intent using broad cast manager.
//        LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
    }
    public boolean isMainController() {
        return controllerID == MASTER_KEY;
    }
    final ExecutorService messageExecutor = Executors.newSingleThreadExecutor();

    /**
     * For BackEnd
     */
    public void sendStatusCommand() {
        if(clientPollingController == null) return;
        int command = (controllerID == MASTER_KEY) ? CommandInfo.STATUS_MAIN_REQUEST.parentCommand : CommandInfo.STATUS_SUB_RESPONSE.parentCommand;
        messageExecutor.submit(()->{
            clientPollingController.sendMessage(command);
        });
    }
    public void sendMessage(int command) {
        if(clientPollingController == null) return;
        clientPollingController.sendMessage(command);
    }
    public void sendMessage(CommandInfo command,int value) {
        messageExecutor.submit(()->{
            if(clientPollingController == null) {
                Log.e(TAG, "Can not send due to error occurred when setup");
                return;
            }
            Bundle bundle = new Bundle();
            CommandInfo commandInfo = CommandInfo.getRequestCommand(command,controllerID == MASTER_KEY);
            switch (commandInfo) {
                case ROUTE_ID_SUB_REQUEST:
                case ROUTE_ID_MAIN_REQUEST:
                    bundle.putInt("routeNum", value);
                    break;
                case DIRECTION_MAIN_REQUEST:
                case DIRECTION_SUB_REQUEST:
                    bundle.putByte("direction", (byte) value);
                    break;
                default:
                    Log.e(TAG, "Value does not exists with command request Nothing to do");
                    break;
            }
            clientPollingController.sendMessage(commandInfo.parentCommand,controllerID == MASTER_KEY, bundle);
            retry();
        });
    }
    public void sendMessage(CommandInfo command,int value1, int value2) {
        messageExecutor.submit(()->{
            if(clientPollingController == null) {
                Log.e(TAG, "Can not send due to error occurred when setup");
                return;
            }
            Bundle bundle = new Bundle();
            CommandInfo commandInfo = CommandInfo.getRequestCommand(command,controllerID == MASTER_KEY);
            switch (commandInfo) {
                case STOP_SEQ_SUB_REQUEST:
                case STOP_SEQ_MAIN_REQUEST:
                    bundle.putInt("stopSeq", value1);
                    bundle.putInt("operationNum",value2);
                    break;
                case DIRECTION_SUB_REQUEST:
                    bundle.putByte("direction", (byte) value1);
                    bundle.putByte("controllerNum", (byte) value2);
                    break;
                default:
                    Log.e(TAG, "Value does not exists with command request. Nothing to do");
                    break;
            }
            clientPollingController.sendMessage(commandInfo.parentCommand,controllerID == MASTER_KEY, bundle);
            retry();
        });
    }
    public void sendMessage(CommandInfo command,boolean forceSetMainController,int value) {
        messageExecutor.submit(()->{
            if(clientPollingController == null) {
                Log.e(TAG, "Can not send due to error occurred when setup");
                return;
            }
            Bundle bundle = new Bundle();
            CommandInfo commandInfo = CommandInfo.getRequestCommand(command,forceSetMainController);
            switch (commandInfo) {
                case ROUTE_ID_SUB_REQUEST:
                case ROUTE_ID_MAIN_REQUEST:
                    bundle.putInt("routeNum", value);
                    break;
                case DIRECTION_MAIN_REQUEST:
                    bundle.putByte("direction", (byte) value);
                    break;
                default:
                    Log.e(TAG, "Value does not exists with command request Nothing to do");
                    break;
            }
            clientPollingController.sendMessage(commandInfo.parentCommand,forceSetMainController, bundle);
            retry();
        });
    }
    public void sendMessage(CommandInfo command,boolean forceSetMainController, int value1, int value2) {
        messageExecutor.submit(()->{
            if(clientPollingController == null) {
                Log.e(TAG, "Can not send due to error occurred when setup");
                return;
            }
            Bundle bundle = new Bundle();
            CommandInfo commandInfo = CommandInfo.getRequestCommand(command,forceSetMainController);
            switch (commandInfo) {
                case STOP_SEQ_SUB_REQUEST:
                case STOP_SEQ_MAIN_REQUEST:
                    bundle.putInt("stopSeq", value1);
                    bundle.putInt("operationNum",value2);
                    break;
                case DIRECTION_SUB_REQUEST:
                    bundle.putByte("direction", (byte) value1);
                    bundle.putByte("controllerNum", (byte) value2);
                    break;
                default:
                    Log.e(TAG, "Value does not exists with command request. Nothing to do");
                    break;
            }
            clientPollingController.sendMessage(commandInfo.parentCommand,forceSetMainController, bundle);
            retry();
        });
    }
    public void sendMessage(int command,boolean isMainCtrl, Bundle bundle) {
        if(clientPollingController == null) return;
        clientPollingController.sendMessage(command,isMainCtrl, bundle);
    }




    @RequiresApi(api = Build.VERSION_CODES.N)
    public void close() {
        if(clientPollingController == null) return;
        clientPollingController.getClientControllerList().forEach(ClientController::doClose);
        clientPollingController.getClientSocketHandler().removeCallbacksAndMessages(null);
        clientPollingController = null;
        if(serverPollingController == null) return;
        serverPollingController.getServerControllerList().forEach(ServerController::doClose);
        serverPollingController.getSocketHandler().removeCallbacksAndMessages(null);
        serverPollingController = null;
    }


    //------------------ This is library, please contact to Email:khoi.nguyen.ts.lecip.jp
    // if having bug or want to maintenance---------------------//
    @RequiresApi(api = Build.VERSION_CODES.N)
    public void run() {
            close();
            setup();
            polling();
            handleMessage(messageConsumer);
    }

    void handleMessage(Consumer<Message> callback) {
        if(serverPollingController == null) return;
        serverPollingController.setCallback(callback);
    }
    void setup() {
        if(controllerID == MASTER_KEY) {
            setupMainController();
        } else {
            setupSubController();
        }
    }

    void setupSubController() {
        for (int i = 0; i < controllerArr.length; i++) {
            if(controllerArr[i] >= 0 && controllerID == (i+1)) {
                String name = IPInfo.findName(controllerID);
                String IP = IPInfo.MAIN.IP;
                int portMain = PortInfo.findPort(i, true);
                Log.d(TAG, "New ClientController will be created with IP" + IP + "port: "+portMain +" index: "+i);
                int portSub = PortInfo.findPort(i,false);
                Log.d(TAG, "New ServerController will be created with port: "+portSub +" index: "+i);
                clientPollingController = new ClientPollingController("Client Polling S-M "+name, Collections.singletonList(new ClientController(IP,portMain)));
                clientPollingController.setSendFactory(new SubFactory());
                serverPollingController = new ServerPollingController("Server Polling S-M "+name, Collections.singletonList(new ServerController(portSub)));
                clientPollingController.setRelationShip(serverPollingController);
            }
        }
    }

    void setupMainController() {
        List<ClientController> clientControllerList = new ArrayList<>();
        List<ServerController> serverControllerList = new ArrayList<>();
        for (int i = 0; i < controllerArr.length; i++) {
            if(controllerArr[i] >= 0 && (i+1) != MASTER_KEY) {
                String IP = IPInfo.fromKey(i+1);
                int portSub = PortInfo.findPort(i, false);
                Log.d(TAG, "New ClientController will be created with IP" + IP + "port: "+portSub +" index: "+i);
                clientControllerList.add(new ClientController(IP,portSub));
                int portMain = PortInfo.findPort(i,true);
                Log.d(TAG, "New ServerController will be created with port: "+portMain +" index: "+i);
                serverControllerList.add(new ServerController(portMain));
            }
        }
        clientPollingController = new ClientPollingController("Client Polling M-S", clientControllerList);
        clientPollingController.setSendFactory(new MainFactory());
        serverPollingController = new ServerPollingController("Server Polling M-S", serverControllerList);
        clientPollingController.setRelationShip(serverPollingController);

    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    void polling() {
        serverPollingController.start();
        //TODO: chain???
        clientPollingController.start();
    }

    void retry() {
        int counter = 0;
        final int COUNTER_MAX = 5;
        while (counter++ < COUNTER_MAX) {//Try 5 times
            Log.d(TAG, "Checking ......");
                try {
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            Log.d(TAG,"Sleep after 500ms with status"+isDone());
        }
        Log.d(TAG, "Check done with status "+isDone());
        Log.d(TAG, "Back with status command ");
        sendStatusCommand();
    }
    boolean isDone() {
        return serverPollingController.isReceiveDone() && clientPollingController.isSendDone();
    }

    public SocketControllerManager set(int[] controllerArr, int controllerID) {
        this.controllerArr = controllerArr;
        this.controllerID = controllerID;
        return this;
    }

}
