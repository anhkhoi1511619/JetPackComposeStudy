package com.example.jetpackcomposeexample.model.train.factory;

import android.os.Bundle;

import com.example.jetpackcomposeexample.controller.train.CommandInfo;
import com.example.jetpackcomposeexample.model.train.TrainTemp;
import com.example.jetpackcomposeexample.model.train.dto.RouteRequest;
import com.example.jetpackcomposeexample.model.train.dto.StopStationRequest;
import com.example.jetpackcomposeexample.model.train.dto.SubControllerResponse;
import com.example.jetpackcomposeexample.model.train.dto.SubDirectionRequest;

public class SubFactory implements SendFactory {
    static final String TAG = SubFactory.class.getSimpleName();
    Send status(int command, int sequence) {
        SubControllerResponse statusDataRequest = new SubControllerResponse();
        statusDataRequest.status = TrainTemp.statusResponse;
        statusDataRequest.currentRouteId = TrainTemp.currentRouteId;
        statusDataRequest.currentStopSeq = TrainTemp.currentStopSeq;
        statusDataRequest.errorCode = TrainTemp.errorCodeResponse;
        statusDataRequest.setCommand(command);
        statusDataRequest.setSequenceNum(sequence);
        return statusDataRequest;
    }
    Send route(int command, int sequence, Bundle bundle) {
        if(bundle == null) bundle = new Bundle();
        RouteRequest route = new RouteRequest();
        route.currentRouteId = bundle.getInt("routeNum", TrainTemp.currentRouteId);
        route.setCommand(command);
        route.setSequenceNum(sequence);
        return route;
    }
    Send stopStation(int command, int sequence, Bundle bundle) {
        if(bundle == null) bundle = new Bundle();
        StopStationRequest stopStation = new StopStationRequest();
        stopStation.stopSeq = bundle.getInt("stopSeq",TrainTemp.currentStopSeq);
        stopStation.operationNum =  bundle.getInt("operationNum",TrainTemp.operationNum);
        stopStation.setCommand(command);
        stopStation.setSequenceNum(sequence);
        return stopStation;
    }
    Send direction(int command, int sequence, Bundle bundle) {
        if(bundle == null) bundle = new Bundle();
        SubDirectionRequest direction = new SubDirectionRequest();
        direction.direction = (byte) bundle.getInt("direction", TrainTemp.directionNum);//TODO: Fix here
        direction.controllerNumber = (byte) bundle.getInt("controllerNum", TrainTemp.controllerNumber);//TODO: Fix here
        direction.setCommand(command);
        direction.setSequenceNum(sequence);
        return direction;
    }
    @Override
    public Send fill(int command, int sequence, Object obj) {
        if(!CommandInfo.isExists(command)) command = 1;
        switch (CommandInfo.get(command)) {
            case ROUTE_ID_SUB_REQUEST:
                return route(command, sequence, (Bundle) obj);
            case STOP_SEQ_SUB_REQUEST:
                return stopStation(command, sequence, (Bundle) obj);
            case DIRECTION_SUB_REQUEST:
                return direction(command, sequence, (Bundle) obj);
            default:
                return status(command, sequence);
        }
    }
}
