package com.example.jetpackcomposeexample.model.train.factory;

import android.os.Bundle;

import com.example.jetpackcomposeexample.controller.train.CommandInfo;
import com.example.jetpackcomposeexample.model.train.TrainTemp;
import com.example.jetpackcomposeexample.model.train.dto.MainControllerResponse;
import com.example.jetpackcomposeexample.model.train.dto.MainControllerStatusRequest;
import com.example.jetpackcomposeexample.model.train.dto.MainDirectionRequest;
import com.example.jetpackcomposeexample.model.train.dto.RouteRequest;
import com.example.jetpackcomposeexample.model.train.dto.StopStationRequest;


public class MainFactory implements SendFactory {
    Send status(int sequence) {
        MainControllerStatusRequest statusDataRequest = new MainControllerStatusRequest();
        statusDataRequest.permissionFTP = TrainTemp.permissionFTP;
        statusDataRequest.status = TrainTemp.statusRequest;
        statusDataRequest.currentRouteId = TrainTemp.currentRouteId;
        statusDataRequest.currentStopSeq = TrainTemp.currentStopSeq;
        statusDataRequest.mainErrorCode = TrainTemp.errorCodeMRequest;//TODO: Fix here
        statusDataRequest.sub1ErrorCode = TrainTemp.errorCodeS1Request;//TODO: Fix here
        statusDataRequest.sub2ErrorCode = TrainTemp.errorCodeS2Request;//TODO: Fix here
        statusDataRequest.sub3ErrorCode = TrainTemp.errorCodeS3Request;//TODO: Fix here
        statusDataRequest.subBoardError = TrainTemp.errorCodeSubBoard;
        statusDataRequest.setSequenceNum(sequence);
        return statusDataRequest;
    }

    Send route(int command, int sequence,Bundle bundle) {
        if(bundle == null) bundle = new Bundle();
        RouteRequest route = new RouteRequest();
        route.currentRouteId = bundle.getInt("routeNum", TrainTemp.currentRouteId);
        route.setCommand(command);
        route.setSequenceNum(sequence);
        return route;
    }
    Send stopStation(int command, int sequence,Bundle bundle) {
        if(bundle == null) bundle = new Bundle();
        StopStationRequest stopStation = new StopStationRequest();
        stopStation.stopSeq = bundle.getInt("stopSeq",TrainTemp.currentStopSeq);
        stopStation.operationNum =  bundle.getInt("operationNum",TrainTemp.operationNum);
        stopStation.setCommand(command);
        stopStation.setSequenceNum(sequence);
        return stopStation;
    }

    Send direction(int command, int sequence,Bundle bundle) {
        if(bundle == null) bundle = new Bundle();
        MainDirectionRequest direction = new MainDirectionRequest();
        direction.direction = (byte) bundle.getInt("direction", TrainTemp.directionNum);//TODO: Fix here
        direction.setCommand(command);
        direction.setSequenceNum(sequence);
        return direction;
    }
    Send responseWhenReceiveSubReq(int command, int sequence, Bundle bundle) {
        if(bundle == null) bundle = new Bundle();
        MainControllerResponse response = new MainControllerResponse();
        response.isHandShake = TrainTemp.isHandShake;//TODO: Fix here
        response.setCommand(command);
        response.setSequenceNum(sequence);
        return response;
    }

    @Override
    public Send fill(int command, int sequence, Object obj) {
        if(!CommandInfo.isExists(command)) command = 0;
        switch (CommandInfo.get(command)) {
            case ROUTE_ID_MAIN_REQUEST:
                return route(command, sequence, (Bundle) obj);
            case STOP_SEQ_MAIN_REQUEST:
                return stopStation(command, sequence, (Bundle) obj);
            case DIRECTION_MAIN_REQUEST:
                return direction(command, sequence, (Bundle) obj);
            case ROUTE_ID_MAIN_RESPONSE:
            case STOP_SEQ_MAIN_RESPONSE:
            case DIRECTION_MAIN_RESPONSE:
                return responseWhenReceiveSubReq(command, sequence, (Bundle) obj);
            default:
                return status(sequence);
        }
    }
}
