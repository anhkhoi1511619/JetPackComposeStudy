package com.example.jetpackcomposeexample.model.bus;

public class BusData {
    int staffNo;
    int routeID;

    int firstDate;
    int stationNo;

    int signalStrength;

    public BusData() {
        this.staffNo = 123456;
        this.routeID = 12345678;
        this.firstDate = 123456;
        this.stationNo = 1234;
        this.signalStrength = 1234;
    }

    public int getStaffNo() {
        return staffNo;
    }

    public void setStaffNo(int staffNo) {
        this.staffNo = staffNo;
    }

    public int getRouteID() {
        return routeID;
    }

    public void setRouteID(int routeID) {
        this.routeID = routeID;
    }

    public int getFirstDate() {
        return firstDate;
    }

    public void setFirstDate(int firstDate) {
        this.firstDate = firstDate;
    }

    public int getStationNo() {
        return stationNo;
    }

    public void setStationNo(int stationNo) {
        this.stationNo = stationNo;
    }

    public int getSignalStrength() {
        return signalStrength;
    }

    public void setSignalStrength(int signalStrength) {
        this.signalStrength = signalStrength;
    }
}
